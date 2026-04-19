package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor.sub_processors

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import io.github.dawidprosba.aergiadevkit.api.registries.annotations.HytaleComponent
import io.github.dawidprosba.aergiadevkit.ksp.extensions.findClassesWithAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.generation.AbstractPipelineProcessor
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.GenerateCodec
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps.FindClassesWithAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps.FindClassesWithAnnotationHasPropertyAnnotated
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps.FindSourceFilesWithAnnotationStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps.ValidateAnnotationOnCorrectTarget
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.RegistryEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.annotationArguments
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.qualifiedNameString
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.sub_processors.HytaleComponentPipelineProcessor
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

class HytaleBuilderCodecPipelineProcessor(resolver: Resolver) :
    AbstractPipelineProcessor(resolver) {
    val annotationKClass = GenerateCodec::class
    val sourceFiles: MutableSet<KSFile> = mutableSetOf()

    /**
     * If any of those annotation is applied to a class,
     * It will treat the class as it has the `@GenerateCodec` annotation applied.
     */
    val implicitAnnotatedKClasses = listOf(
        HytaleComponent::class
    )

    val codecPropertyAnnotationKClass = CodecProperty::class

    override fun process(): Set<KSFile> {
        return pipeline
            .next { internalStepFindClasses() }
            .next { internalStepStoreEntries(it) }
            .next { stepFindSourceFiles(it) }
            .next { internalStepStoreSourceFiles(it) }
            .result()
    }

    private fun internalStepFindClasses(): List<KSClassDeclaration> {
        val uniqueDeclarations = mutableSetOf<KSClassDeclaration>()

        // Get Explicitly Annotated classes
        uniqueDeclarations.addAll(FindClassesWithAnnotation(resolver).process(annotationKClass))

        // Get Implicitly Annotated classes
        implicitAnnotatedKClasses.forEach { implicitClassAnnotation ->
            val implicitlyAnnotatedDeclarations = FindClassesWithAnnotationHasPropertyAnnotated(
                resolver,
                codecPropertyAnnotationKClass
            ).process(implicitClassAnnotation)

            uniqueDeclarations.addAll(implicitlyAnnotatedDeclarations)
        }

        return uniqueDeclarations.toList()
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

    private fun stepFindSourceFiles(annotatedClasses: List<KSAnnotated>): Set<KSFile> {
        return FindSourceFilesWithAnnotationStep { deferredSymbols.addAll(it) }.process(
            annotatedClasses
        )
    }

    private fun internalStepStoreSourceFiles(additionalSourceFiles: Set<KSFile>): Set<KSFile> {
        sourceFiles += additionalSourceFiles

        return sourceFiles
    }

    companion object {
        val entries: MutableList<RegistryEntryMetadata> = mutableListOf()
    }

}