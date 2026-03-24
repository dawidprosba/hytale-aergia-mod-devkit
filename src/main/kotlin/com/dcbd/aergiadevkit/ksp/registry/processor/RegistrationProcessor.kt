package com.dcbd.aergiadevkit.ksp.registry.processor

import com.dcbd.aergiadevkit.ksp.registry.annotations.RegisterComponent
import com.dcbd.aergiadevkit.ksp.registry.annotations.RegisterInteraction
import com.dcbd.aergiadevkit.ksp.registry.annotations.RegisterSystem
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate

class RegistrationProcessor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val annotationNames = listOf(
        RegisterInteraction::class.qualifiedName!!,
        RegisterComponent::class.qualifiedName!!,
        RegisterSystem::class.qualifiedName!!
    )

    private val outputPackage = environment.options["registriesOutputPackage"]
        ?: throw IllegalArgumentException("Missing required option: registriesOutputPackage add it under build.gradle.kts, ksp {..here..}")
    private val pluginClass = environment.options["pluginClass"]
        ?: throw IllegalArgumentException("Missing required option: pluginClass — add arg(\"pluginClass\", \"com.example.YourModClass\") to ksp { } in build.gradle.kts")
    private val codeGenerator: CodeGenerator = environment.codeGenerator

    private val outputObjects : MutableMap<String,String> = mutableMapOf(
        RegisterInteraction::class.qualifiedName!! to "InteractionRegistryGenerated",
        RegisterComponent::class.qualifiedName!! to "ComponentRegistryGenerated",
        RegisterSystem::class.qualifiedName!! to "SystemRegistryGenerated"
    )

    private val collectedEntriesByAnnotation: MutableMap<String, MutableMap<String, RegistryEntryMetadata>> =
        annotationNames.associateWith { mutableMapOf<String, RegistryEntryMetadata>() }.toMutableMap()
    private val sourceFilesByAnnotation: MutableMap<String, MutableSet<KSFile>> =
        annotationNames.associateWith { mutableSetOf<KSFile>() }.toMutableMap()

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

        return deferredSymbols
    }

    override fun finish() {
        annotationNames.forEach { annotationQualifiedName ->
            val entries = collectedEntriesByAnnotation
                .getValue(annotationQualifiedName)
                .values
                .sortedBy { it.qualifiedName }

            val content = when (annotationQualifiedName) {
                RegisterInteraction::class.qualifiedName!! -> interactionTemplate(outputPackage, entries, pluginClass)
                RegisterComponent::class.qualifiedName!! -> componentTemplate(outputPackage, entries, pluginClass)
                RegisterSystem::class.qualifiedName!! -> systemTemplate(outputPackage, entries, pluginClass)
                else -> error("Unknown annotation: $annotationQualifiedName")
            }

            val sourceFiles = sourceFilesByAnnotation.getValue(annotationQualifiedName).toTypedArray()
            codeGenerator.createNewFile(
                Dependencies(true, *sourceFiles),
                outputPackage,
                outputObjects.getOrDefault(annotationQualifiedName, "GeneratedRegistry")
            ).bufferedWriter().use { writer ->
                writer.write(content)
            }
        }
    }

    private fun findAnnotatedClasses(
        resolver: Resolver,
        annotationName: String
    ): List<KSClassDeclaration> {
        return resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSClassDeclaration>()
            .toList()
    }
}
