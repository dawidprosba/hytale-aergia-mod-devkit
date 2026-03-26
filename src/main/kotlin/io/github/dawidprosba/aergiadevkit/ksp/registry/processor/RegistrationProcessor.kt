package io.github.dawidprosba.aergiadevkit.ksp.registry.processor

import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterComponent
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterGlobalEvent
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterInteraction
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterSystem
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

class RegistrationProcessor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val interactionAnnotation = RegisterInteraction::class.qualifiedName!!
    private val componentAnnotation = RegisterComponent::class.qualifiedName!!
    private val systemAnnotation = RegisterSystem::class.qualifiedName!!
    private val globalEventAnnotation = RegisterGlobalEvent::class.qualifiedName!!
    private val annotationNames = listOf(interactionAnnotation, componentAnnotation, systemAnnotation)

    private val outputPackage = environment.options["registriesOutputPackage"]
        ?: throw IllegalArgumentException("Missing required option: registriesOutputPackage add it under build.gradle.kts, ksp {..here..}")
    private val pluginClass = environment.options["pluginClass"]
        ?: throw IllegalArgumentException("Missing required option: pluginClass — add arg(\"pluginClass\", \"com.example.YourModClass\") to ksp { } in build.gradle.kts")
    private val codeGenerator: CodeGenerator = environment.codeGenerator

    private val collectedEntriesByAnnotation: MutableMap<String, MutableMap<String, RegistryEntryMetadata>> =
        annotationNames.associateWith { mutableMapOf<String, RegistryEntryMetadata>() }.toMutableMap()
    private val sourceFilesByAnnotation: MutableMap<String, MutableSet<KSFile>> =
        annotationNames.associateWith { mutableSetOf<KSFile>() }.toMutableMap()

    private val collectedGlobalEventEntries = mutableMapOf<String, GlobalEventEntryMetadata>()
    private val globalEventSourceFiles = mutableSetOf<KSFile>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val deferredSymbols = mutableListOf<KSAnnotated>()

        annotationNames.forEach { annotationQualifiedName ->
            val symbols = findAnnotatedClasses(resolver, annotationQualifiedName)
            val deferred = symbols.filterNot { it.validate() }
            val validSymbols = symbols.filter { it.validate() }

            if (deferred.isNotEmpty()) {
                deferredSymbols += deferred
            }

            validSymbols.forEach { declaration ->
                val qualifiedName = parseQualifiedName(declaration)
                val arguments = getAnnotationArguments(declaration, annotationQualifiedName)
                val isEnabled = arguments.getOrDefault("enabled", true) as Boolean

                collectedEntriesByAnnotation
                    .getValue(annotationQualifiedName)[qualifiedName] =
                    RegistryEntryMetadata(qualifiedName, isEnabled)

                declaration.containingFile?.let { file ->
                    sourceFilesByAnnotation.getValue(annotationQualifiedName).add(file)
                }
            }
        }

        val globalEventSymbols = resolver.getSymbolsWithAnnotation(globalEventAnnotation)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        deferredSymbols += globalEventSymbols.filterNot { it.validate() }

        globalEventSymbols.filter { it.validate() }.forEach { fn ->
            val fqn = fn.qualifiedName?.asString() ?: return@forEach
            val args = fn.annotations
                .first { it.annotationType.resolve().declaration.qualifiedName?.asString() == globalEventAnnotation }
                .arguments.associate { it.name?.asString().orEmpty() to it.value }

            val eventClassQualifiedName = (args["eventClass"] as? KSType)
                ?.declaration?.qualifiedName?.asString()
                ?: error("Missing eventClass for @RegisterGlobalEvent on $fqn")

            val enabled = args.getOrDefault("enabled", true) as Boolean

            val companionDecl = fn.parentDeclaration as? KSClassDeclaration
            val outerClass = companionDecl?.parentDeclaration as? KSClassDeclaration
            val containingClassQualifiedName = outerClass?.qualifiedName?.asString()
                ?: companionDecl?.qualifiedName?.asString()
                ?: error("Cannot resolve containing class for @RegisterGlobalEvent on $fqn")

            collectedGlobalEventEntries[fqn] = GlobalEventEntryMetadata(
                functionQualifiedName = fqn,
                containingClassQualifiedName = containingClassQualifiedName,
                functionName = fn.simpleName.asString(),
                eventClassQualifiedName = eventClassQualifiedName,
                hasEventParam = fn.parameters.isNotEmpty(),
                enabled = enabled
            )
            fn.containingFile?.let { globalEventSourceFiles.add(it) }
        }

        return deferredSymbols
    }

    override fun finish() {
        InteractionRegistryGenerator(
            outputPackage = outputPackage,
            pluginClass = pluginClass,
            entries = collectedEntriesByAnnotation.getValue(interactionAnnotation).values.sortedBy { it.qualifiedName },
            sourceFiles = sourceFilesByAnnotation.getValue(interactionAnnotation).toTypedArray(),
            codeGenerator = codeGenerator
        ).generate()

        ComponentRegistryGenerator(
            outputPackage = outputPackage,
            pluginClass = pluginClass,
            entries = collectedEntriesByAnnotation.getValue(componentAnnotation).values.sortedBy { it.qualifiedName },
            sourceFiles = sourceFilesByAnnotation.getValue(componentAnnotation).toTypedArray(),
            codeGenerator = codeGenerator
        ).generate()

        SystemRegistryGenerator(
            outputPackage = outputPackage,
            pluginClass = pluginClass,
            entries = collectedEntriesByAnnotation.getValue(systemAnnotation).values.sortedBy { it.qualifiedName },
            sourceFiles = sourceFilesByAnnotation.getValue(systemAnnotation).toTypedArray(),
            codeGenerator = codeGenerator
        ).generate()

        GlobalEventRegistryGenerator(
            outputPackage = outputPackage,
            pluginClass = pluginClass,
            entries = collectedGlobalEventEntries.values.sortedBy { it.functionQualifiedName },
            sourceFiles = globalEventSourceFiles.toTypedArray(),
            codeGenerator = codeGenerator
        ).generate()
    }

    // --- Finders ---

    private fun findAnnotatedClasses(
        resolver: Resolver,
        annotationName: String
    ): List<KSClassDeclaration> {
        return resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSClassDeclaration>()
            .toList()
    }
}
