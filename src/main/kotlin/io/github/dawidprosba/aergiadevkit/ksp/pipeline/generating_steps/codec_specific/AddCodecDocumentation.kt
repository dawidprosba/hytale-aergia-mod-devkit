package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific

import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.PropertyCodecMetadata

class AddCodecDocumentation (val builder : CodeBlock.Builder): PipelineStep<PropertyCodecMetadata, CodeBlock.Builder>{
    override fun process(input: PropertyCodecMetadata): CodeBlock.Builder {
        val documentation = input.annotationArgs["documentation"] as? String? ?: error("Missing documentation.")

        return builder.add(".documentation(%S)\n", documentation)
    }
}