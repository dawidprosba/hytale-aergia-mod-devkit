package io.github.dawidprosba.aergiadevkit.ksp.registry.processor.sub_processors

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import io.github.dawidprosba.aergiadevkit.api.registries.annotations.HytaleComponent
import io.github.dawidprosba.aergiadevkit.ksp.generation.AbstractPipelineProcessor
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps.FindClassesWithAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps.FindSourceFilesWithAnnotationStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps.ValidateAnnotationOnCorrectTarget
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.RegistryEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.annotationArguments
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.qualifiedNameString
import java.util.Collections.emptyList

class HytaleComponentPipelineProcessor(resolver: Resolver) : AbstractPipelineProcessor(resolver) {
    val annotationKClass = HytaleComponent::class
    val correctTargets = listOf(
        "com.hypixel.hytale.component.Component"
    )

    val sourceFiles: MutableSet<KSFile> = mutableSetOf()
    val entries: MutableList<RegistryEntryMetadata> = mutableListOf()

    override fun process(): Set<KSFile> {
        return pipeline
            .next { stepFindClassesWithAnnotation() }
            .next { internalStepStoreEntries(it)}
            .next { stepValidateAnnotationOnCorrectTarget(it) }
            .next { stepFindSourceFiles(it) }
            .next { internalStepStoreSourceFiles(it) }
            .result()
    }

    private fun stepFindClassesWithAnnotation(): List<KSClassDeclaration> {
        return FindClassesWithAnnotation(resolver).process(annotationKClass)
    }

    private fun stepValidateAnnotationOnCorrectTarget(annotatedClasses: List<KSClassDeclaration>): List<KSAnnotated> {
        return ValidateAnnotationOnCorrectTarget(correctTargets).process(annotatedClasses)
    }

    private fun stepFindSourceFiles(annotatedClasses: List<KSAnnotated>): Set<KSFile> {
        return FindSourceFilesWithAnnotationStep { deferredSymbols.addAll(it) }.process(annotatedClasses)
    }

    private fun internalStepStoreSourceFiles(additionalSourceFiles: Set<KSFile>): Set<KSFile> {
        sourceFiles += additionalSourceFiles

        return sourceFiles
    }

    private fun internalStepStoreEntries(declarations: List<KSClassDeclaration>): List<KSClassDeclaration> {
        declarations.filter { it.validate() }.forEach {
            val qualifiedName = it.qualifiedNameString()
            val arguments = it.annotationArguments(annotationKClass.qualifiedName!!)
            val isEnabled = arguments.getOrDefault("enabled", true) as Boolean

            entries += RegistryEntryMetadata(qualifiedName, isEnabled)
        }
        return declarations
    }

}