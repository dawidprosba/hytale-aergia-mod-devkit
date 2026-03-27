package io.github.dawidprosba.aergiadevkit.ksp.registry.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterComponent
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterEvent
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterGlobalEvent
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterInteraction
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterSystem
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.EventEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.GlobalEventEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.RegistryEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.generators.ComponentRegistryGenerator
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.generators.EventRegistryGenerator
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.generators.GlobalEventRegistryGenerator
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.generators.InteractionRegistryGenerator
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.generators.SystemRegistryGenerator

class RegistrationProcessor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val interactionAnnotation = RegisterInteraction::class.qualifiedName!!
    private val componentAnnotation = RegisterComponent::class.qualifiedName!!
    private val systemAnnotation = RegisterSystem::class.qualifiedName!!
    private val globalEventAnnotation = RegisterGlobalEvent::class.qualifiedName!!
    private val eventAnnotation = RegisterEvent::class.qualifiedName!!
    private val simpleAnnotations = listOf(interactionAnnotation, componentAnnotation, systemAnnotation)

    private val outputPackage = environment.options["registriesOutputPackage"]
        ?: throw IllegalArgumentException("Missing required option: registriesOutputPackage add it under build.gradle.kts, ksp {..here..}")
    private val pluginClass = environment.options["pluginClass"]
        ?: throw IllegalArgumentException("Missing required option: pluginClass — add arg(\"pluginClass\", \"com.example.YourModClass\") to ksp { } in build.gradle.kts")
    private val codeGenerator: CodeGenerator = environment.codeGenerator

    private val collectedEntriesByAnnotation: MutableMap<String, MutableMap<String, RegistryEntryMetadata>> =
        simpleAnnotations.associateWith { mutableMapOf<String, RegistryEntryMetadata>() }.toMutableMap()
    private val sourceFilesByAnnotation: MutableMap<String, MutableSet<KSFile>> =
        simpleAnnotations.associateWith { mutableSetOf<KSFile>() }.toMutableMap()

    private val collectedGlobalEventEntries = mutableMapOf<String, GlobalEventEntryMetadata>()
    private val globalEventSourceFiles = mutableSetOf<KSFile>()

    private val collectedEventEntries = mutableMapOf<String, EventEntryMetadata>()
    private val eventSourceFiles = mutableSetOf<KSFile>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val deferredSymbols = mutableListOf<KSAnnotated>()

        simpleAnnotations.forEach { annotationQualifiedName ->
            val symbols = resolver.getSymbolsWithAnnotation(annotationQualifiedName)
                .filterIsInstance<KSClassDeclaration>()
                .toList()

            deferredSymbols += symbols.filterNot { it.validate() }

            symbols.filter { it.validate() }.forEach { declaration ->
                val qualifiedName = declaration.qualifiedNameString()
                val arguments = declaration.annotationArguments(annotationQualifiedName)
                val isEnabled = arguments.getOrDefault("enabled", true) as Boolean

                collectedEntriesByAnnotation.getValue(annotationQualifiedName)[qualifiedName] =
                    RegistryEntryMetadata(qualifiedName, isEnabled)
                declaration.containingFile?.let { sourceFilesByAnnotation.getValue(annotationQualifiedName).add(it) }
            }
        }

        deferredSymbols += processEventFunctions(resolver, globalEventAnnotation) { fn, fqn, args ->
            val eventClassQualifiedName = (args["eventClass"] as? KSType)
                ?.declaration?.qualifiedName?.asString()
                ?: error("Missing eventClass for @RegisterGlobalEvent on $fqn")
            val enabled = args.getOrDefault("enabled", true) as Boolean

            collectedGlobalEventEntries[fqn] = GlobalEventEntryMetadata(
                functionQualifiedName = fqn,
                containingClassQualifiedName = fn.containingClassQualifiedName(),
                functionName = fn.simpleName.asString(),
                eventClassQualifiedName = eventClassQualifiedName,
                eventParamTypeName = fn.parameters.firstOrNull()?.type?.resolve()?.toKotlinString(),
                hasEventParam = fn.parameters.isNotEmpty(),
                enabled = enabled,
            )
            fn.containingFile?.let { globalEventSourceFiles.add(it) }
        }

        deferredSymbols += processEventFunctions(resolver, eventAnnotation) { fn, fqn, args ->
            val eventClassQualifiedName = (args["eventClass"] as? KSType)
                ?.declaration?.qualifiedName?.asString()
                ?: error("Missing eventClass for @RegisterEvent on $fqn")
            val subjectClassQualifiedName = (args["subject"] as? KSType)
                ?.declaration?.qualifiedName?.asString()
                ?: error("Missing subject for @RegisterEvent on $fqn")
            val enabled = args.getOrDefault("enabled", true) as Boolean

            collectedEventEntries[fqn] = EventEntryMetadata(
                functionQualifiedName = fqn,
                containingClassQualifiedName = fn.containingClassQualifiedName(),
                functionName = fn.simpleName.asString(),
                eventClassQualifiedName = eventClassQualifiedName,
                eventParamTypeName = fn.parameters.firstOrNull()?.type?.resolve()?.toKotlinString(),
                subjectClassQualifiedName = subjectClassQualifiedName,
                hasEventParam = fn.parameters.isNotEmpty(),
                enabled = enabled,
            )
            fn.containingFile?.let { eventSourceFiles.add(it) }
        }

        return deferredSymbols
    }

    override fun finish() {
        InteractionRegistryGenerator(
            outputPackage = outputPackage,
            pluginClass = pluginClass,
            entries = collectedEntriesByAnnotation.getValue(interactionAnnotation).values.sortedBy { it.qualifiedName },
            sourceFiles = sourceFilesByAnnotation.getValue(interactionAnnotation).toTypedArray(),
            codeGenerator = codeGenerator,
        ).generate()

        ComponentRegistryGenerator(
            outputPackage = outputPackage,
            pluginClass = pluginClass,
            entries = collectedEntriesByAnnotation.getValue(componentAnnotation).values.sortedBy { it.qualifiedName },
            sourceFiles = sourceFilesByAnnotation.getValue(componentAnnotation).toTypedArray(),
            codeGenerator = codeGenerator,
        ).generate()

        SystemRegistryGenerator(
            outputPackage = outputPackage,
            pluginClass = pluginClass,
            entries = collectedEntriesByAnnotation.getValue(systemAnnotation).values.sortedBy { it.qualifiedName },
            sourceFiles = sourceFilesByAnnotation.getValue(systemAnnotation).toTypedArray(),
            codeGenerator = codeGenerator,
        ).generate()

        GlobalEventRegistryGenerator(
            outputPackage = outputPackage,
            pluginClass = pluginClass,
            entries = collectedGlobalEventEntries.values.sortedBy { it.functionQualifiedName },
            sourceFiles = globalEventSourceFiles.toTypedArray(),
            codeGenerator = codeGenerator,
        ).generate()

        EventRegistryGenerator(
            outputPackage = outputPackage,
            pluginClass = pluginClass,
            entries = collectedEventEntries.values.sortedBy { it.functionQualifiedName },
            sourceFiles = eventSourceFiles.toTypedArray(),
            codeGenerator = codeGenerator,
        ).generate()
    }

    private fun processEventFunctions(
        resolver: Resolver,
        annotationName: String,
        onValid: (fn: KSFunctionDeclaration, fqn: String, args: Map<String, Any?>) -> Unit,
    ): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        symbols.filter { it.validate() }.forEach { fn ->
            val fqn = fn.qualifiedName?.asString() ?: return@forEach
            onValid(fn, fqn, fn.annotationArguments(annotationName))
        }

        return symbols.filterNot { it.validate() }
    }
}
