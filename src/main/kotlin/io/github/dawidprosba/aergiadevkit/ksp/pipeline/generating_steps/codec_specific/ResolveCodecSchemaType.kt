package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.hytale.HYTALE_CODEC_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep

private val PRIMITIVE_HYTALE_CODECS = mapOf(
    "kotlin.String" to "STRING",
    "kotlin.Int" to "INT",
    "kotlin.Boolean" to "BOOLEAN",
    "kotlin.Float" to "FLOAT",
    "kotlin.Double" to "DOUBLE",
    "kotlin.Long" to "LONG",
)

class ResolveCodecSchemaType : PipelineStep<ResolveCodecSchemaType.Input, CodeBlock> {
    data class Input(
        val propertyQualifiedName: String,
        val propertyType: KSType
    )

    override fun process(input: Input): CodeBlock {
        return resolveCodecSchemaType(input.propertyQualifiedName, input.propertyType)
    }

    private fun resolveCodecSchemaType(
        propertyQualifiedName: String,
        propertyType: KSType
    ): CodeBlock {
        PRIMITIVE_HYTALE_CODECS[propertyQualifiedName]?.let { constant ->
            return CodeBlock.of("%T.$constant", HYTALE_CODEC_TYPE)
        }

        val typePackage = propertyType.declaration.packageName.asString()
        val typeName = propertyType.declaration.simpleName.asString()

        // If it's not a primitive type, we assume it's a complex type with its own CODEC field e.g. ExampleDataClass.CODEC
        return CodeBlock.of("%T.CODEC", ClassName(typePackage, typeName))
    }
}



