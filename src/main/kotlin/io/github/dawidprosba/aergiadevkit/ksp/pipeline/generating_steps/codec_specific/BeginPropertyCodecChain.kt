package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.extensions.getAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.extensions.getArgs
import io.github.dawidprosba.aergiadevkit.ksp.extensions.hasAnyCompanionProperty
import io.github.dawidprosba.aergiadevkit.ksp.extensions.isInheritedProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytale.HYTALE_CODEC_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.hytale.HYTALE_KEYED_CODEC_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.PropertyCodecMetadata


class BeginPropertyCodecChain(
    val builder: CodeBlock.Builder,
) :
    PipelineStep<PropertyCodecMetadata, CodeBlock.Builder> {

    override fun process(input: PropertyCodecMetadata): CodeBlock.Builder {
        if (input.treatAsInherited) {
            addInheritedPropertyChain(input)
        } else {
            addPropertyChain(input)
        }

        return builder
    }



    private fun addPropertyChain(metadata: PropertyCodecMetadata) {
        builder
            .add(
                ".append(%T(%S, %L, %L),\n",
                HYTALE_KEYED_CODEC_TYPE,
                metadata.codecKey,
                metadata.codecSchemaType,
                metadata.treatAsInherited
            )
            .add("    { config, value -> config.%N = value },\n", metadata.propertyName)
            .add("    { config -> config.%N }\n", metadata.propertyName)
            .add(")\n")
    }

    private fun addInheritedPropertyChain(metadata: PropertyCodecMetadata)  {

        builder.add(
            ".appendInherited(%T(%S, %L, %L),\n",
            HYTALE_KEYED_CODEC_TYPE,
            metadata.codecKey,
            metadata.codecSchemaType,
            metadata.isRequired,
        )
            .add("    { config, value -> config.%N = value },\n", metadata.propertyName)
            .add("    { config -> config.%N },\n", metadata.propertyName)
            .add(
                "    { target, parent -> target.%N = parent.%N }\n",
                metadata.propertyName,
                metadata.propertyName
            )
            .add(")\n")
    }
}