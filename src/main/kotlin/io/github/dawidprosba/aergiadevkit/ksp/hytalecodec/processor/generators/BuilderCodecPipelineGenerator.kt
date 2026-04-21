package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.generation.AbstractPipelineGenerator
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.validators.CodecProjectileValidator
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.validators.CodecRequiredValidator
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.data.CodecGeneratorEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.validators.CODEC_PROJECTILE_VALIDATOR_TEMPLATE
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.validators.CODEC_REQUIRED_VALIDATOR_TEMPLATE
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific.AddCodecDocumentation
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific.BeginPropertyCodecChain
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific.StepAddBuilderInitializer
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.PropertyCodecMetadata
import kotlin.reflect.KClass


/**
 * Generates BuilderCodec File for each entry.
 */
class BuilderCodecPipelineGenerator(options: Options, codeGenerator: CodeGenerator) :
    AbstractPipelineGenerator<BuilderCodecPipelineGenerator.Options>(
        options, codeGenerator
    ) {

    companion object {
        private val supportedValidators: Map<KClass<*>, String> = mapOf(
            CodecRequiredValidator::class to CODEC_REQUIRED_VALIDATOR_TEMPLATE,
            CodecProjectileValidator::class to CODEC_PROJECTILE_VALIDATOR_TEMPLATE
        )
    }

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
            val propertyCodecMetadata = PropertyCodecMetadata.from(
                propertyDeclaration = it,
                classDeclaration = entryMetadata.ksClassDeclaration,
                expectedCodecProperty = options.codecPropertyName
            )
            pipeline.next {
                stepBeginPropertyCodecChain(builder, propertyCodecMetadata)
                stepAddDocumentation(builder, propertyCodecMetadata)
                internalStepAddValidators(builder, propertyCodecMetadata)
            }

        }
        return builder
    }

    private fun internalStepAddValidators(
        builder: CodeBlock.Builder,
        entryMetadata: PropertyCodecMetadata
    ): CodeBlock.Builder {
        supportedValidators.forEach { (validatorClass, template) ->
            supportedValidators.forEach { (validatorClass, template) ->
                val annotation = entryMetadata.propertyDeclaration.annotations.find { annotation ->
                    annotation.shortName.asString() == validatorClass.simpleName
                }
                if (annotation == null) {
                    return@forEach
                }
                builder.add(".addValidator(%L)\n", template)
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

    private fun stepAddDocumentation(
        builder: CodeBlock.Builder,
        propertyCodecMetadata: PropertyCodecMetadata
    ): CodeBlock.Builder {
        return AddCodecDocumentation(
            builder = builder
        ).process(propertyCodecMetadata)
    }


}