package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific

import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.PropertyCodecMetadata

abstract class AnnotationConditionalPropertyStep(
    protected val builder: CodeBlock.Builder
) : PipelineStep<PropertyCodecMetadata, CodeBlock.Builder> {

    abstract val annotationSimpleName: String

    abstract fun applyToBuilder(builder: CodeBlock.Builder)

    override fun process(input: PropertyCodecMetadata): CodeBlock.Builder {
        val hasAnnotation = input.propertyDeclaration.annotations.any { annotation ->
            annotation.shortName.asString() == annotationSimpleName
        }
        if (hasAnnotation) {
            applyToBuilder(builder)
        }
        return builder
    }
}