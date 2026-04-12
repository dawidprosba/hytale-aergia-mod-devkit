package io.github.dawidprosba.aergiadevkit.ksp.registry.processor.generators

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import io.github.dawidprosba.aergiadevkit.api.registries.annotations.HytaleComponent
import io.github.dawidprosba.aergiadevkit.ksp.generation.AbstractGenerator
import io.github.dawidprosba.aergiadevkit.ksp.generator_pipeline.steps.FindClassesWithAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.generator_pipeline.steps.FindSourceFilesWithAnnotationStep
import io.github.dawidprosba.aergiadevkit.ksp.generator_pipeline.steps.ValidateAnnotationOnCorrectTarget

class HytaleComponentGenerator(resolver: Resolver) : AbstractGenerator(resolver) {
    val annotationKClass = HytaleComponent::class
    val correctTargets = listOf(
        "com.hypixel.hytale.component.Component"
    )

    override fun process(): Set<KSAnnotated> {
        return pipeline
            .next { stepFindClassesWithAnnotation() }
            .next { stepValidateAnnotationOnCorrectTarget(it) }
            .next { stepFindSourceFiles(it) }
            .result()
    }

    private fun stepFindClassesWithAnnotation(): List<KSAnnotated> {
        return FindClassesWithAnnotation(resolver).process(annotationKClass)
    }

    private fun stepValidateAnnotationOnCorrectTarget(annotatedClasses: List<KSAnnotated>): List<KSAnnotated> {
        return ValidateAnnotationOnCorrectTarget(correctTargets).process(annotatedClasses)
    }

    private fun stepFindSourceFiles(annotatedClasses: List<KSAnnotated>): Set<KSFile> {
        return FindSourceFilesWithAnnotationStep(
            deferredSymbols::addAll
        ).process(annotatedClasses)
    }
}