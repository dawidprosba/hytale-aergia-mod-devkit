package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.extensions.getAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.extensions.getArgs
import io.github.dawidprosba.aergiadevkit.ksp.extensions.hasAnyCompanionProperty
import io.github.dawidprosba.aergiadevkit.ksp.extensions.isInheritedProperty
import io.github.dawidprosba.aergiadevkit.ksp.generation.AbstractPipelineGenerator
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.data.CodecGeneratorEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific.BeginPropertyCodecChain
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific.ResolveCodecSchemaType
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific.StepAddBuilderInitializer
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.PropertyCodecMetadata

/**
 * Generates BuilderCodec File for each entry.
 */
class BuilderCodecPipelineGenerator(options: Options, codeGenerator: CodeGenerator) :
    AbstractPipelineGenerator<BuilderCodecPipelineGenerator.Options>(
        options, codeGenerator
    ) {

    data class Options(
        val entries: List<CodecGeneratorEntryMetadata>,
        val codecPropertyName: String = "CODEC",
    )


    override fun generate() {
        options.entries.forEach { entryMetadata ->
            pipeline
                .next { stepGenerateInitializer(entryMetadata.ksClassDeclaration) }
                .next { stepAddPropertiesCodecChain(entryMetadata, it) }

        }
    }

    private fun stepGenerateInitializer(classDeclaration: KSClassDeclaration): CodeBlock.Builder {
        val stepOptions = StepAddBuilderInitializer.BuilderInitializerOptions(
            classDeclaration = classDeclaration,
        )
        return StepAddBuilderInitializer(
            stepOptions
        ).process()
    }

    private fun stepAddPropertiesCodecChain(
        entryMetadata: CodecGeneratorEntryMetadata,
        builder: CodeBlock.Builder
    ): CodeBlock.Builder {
        entryMetadata.propertiesMarkedWithCodec.forEach {
            val propertyCodecMetadata = getPropertyCodecMetadata(
                propertyDeclaration = it,
                classDeclaration = entryMetadata.ksClassDeclaration,
                expectedCodecProperty = options.codecPropertyName
            )
            pipeline.next {
                stepBeginPropertyCodecChain(builder, propertyCodecMetadata)

            }

        }
        return builder
    }

    private fun stepBeginPropertyCodecChain(
        builder: CodeBlock.Builder,
        propertyCodecMetadata: PropertyCodecMetadata,
    ): CodeBlock.Builder {
        return BeginPropertyCodecChain(
            builder = builder,
        ).process(
            propertyCodecMetadata
        )
    }



    private fun getPropertyCodecMetadata(
        propertyDeclaration: KSPropertyDeclaration,
        classDeclaration: KSClassDeclaration,
        expectedCodecProperty: String
    ): PropertyCodecMetadata {
        val annotation =
            propertyDeclaration.getAnnotation(CodecProperty::class.simpleName!!)
        val annotationArgs = annotation?.getArgs() ?: emptyMap()
        val propertyName = propertyDeclaration.simpleName.asString()
        val propertyType = propertyDeclaration.type.resolve()
        val propertyQualifiedName = propertyType.declaration.qualifiedName?.asString()
        val hasCodecProperty =
            (propertyDeclaration.type.resolve().declaration as? KSClassDeclaration)
                ?.hasAnyCompanionProperty(expectedCodecProperty)
                ?: false
        val treatAsInherited =
            propertyDeclaration.isInheritedProperty(classDeclaration) || hasCodecProperty
        val isRequired = annotationArgs.getOrDefault("isRequired", true) as Boolean
        val codecSchemaType = ResolveCodecSchemaType().process(
            ResolveCodecSchemaType.Input(
                propertyQualifiedName = propertyQualifiedName ?: "",
                propertyType = propertyType
            )
        )
        val codecKey = propertyName.replaceFirstChar { it.uppercase() }

        return PropertyCodecMetadata(
            annotation = annotation!!,
            annotationArgs = annotationArgs,
            propertyName = propertyName,
            propertyQualifiedName = propertyQualifiedName,
            treatAsInherited = treatAsInherited,
            isRequired = isRequired,
            codecSchemaType = codecSchemaType,
            codecKey = codecKey
        )
    }
}