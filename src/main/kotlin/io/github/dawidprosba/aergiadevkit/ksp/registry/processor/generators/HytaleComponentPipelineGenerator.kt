package io.github.dawidprosba.aergiadevkit.ksp.registry.processor.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.dawidprosba.aergiadevkit.api.registries.services.AergiaComponentRegistrationService
import io.github.dawidprosba.aergiadevkit.ksp.generation.AbstractPipelineGenerator
import io.github.dawidprosba.aergiadevkit.ksp.generation.GeneratorOptions
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.BuildFileSpecStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.BuildRegistryClass
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.CreateFileStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.CreateMetaInfStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.CreateFileOptions
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.CreateMetaInfOptions
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.FileSpecOptions
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.RegistryClassGenerationOptions
import io.github.dawidprosba.aergiadevkit.ksp.hytale.COMPONENT_CLASS
import io.github.dawidprosba.aergiadevkit.ksp.hytale.COMPONENT_REGISTRATION_SERVICE_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.hytale.COMPONENT_REGISTRY_PROXY_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.hytale.COMPONENT_TYPE_CLASS
import io.github.dawidprosba.aergiadevkit.ksp.hytale.ENTITY_STORE_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.hytale.REGISTER_COMPONENT

class HytaleComponentPipelineGenerator(options: GeneratorOptions, codeGenerator: CodeGenerator) :
    AbstractPipelineGenerator<GeneratorOptions>(
        options, codeGenerator
    ) {
    override fun generate() {
        pipeline.next {
            stepGenerateComponentRegistryClass()
        }.next {
            stepGenerateSourceFile(it)
        }.next {
            stepCreateFile(it)
        }.next {
            stepCreateMetaInfFile()
        }
    }


    fun stepGenerateComponentRegistryClass(): TypeSpec {
        val registryOptions = RegistryClassGenerationOptions(
            options.pluginClass,
            options.outputClassName,
            COMPONENT_REGISTRATION_SERVICE_TYPE,
            options.entries,
            COMPONENT_CLASS,
            ENTITY_STORE_TYPE,
            COMPONENT_TYPE_CLASS,
            REGISTER_COMPONENT,
            COMPONENT_REGISTRY_PROXY_TYPE,
        )
        return BuildRegistryClass().process(registryOptions)
    }

    fun stepGenerateSourceFile(content: TypeSpec): FileSpec {
        val options = FileSpecOptions(
            outputPackage = options.outputPackage,
            outputClassName = options.outputClassName,
            content = content
        )

        return BuildFileSpecStep().process(options)
    }

    fun stepCreateFile(fileSpec: FileSpec): Boolean {
        val options = CreateFileOptions(
            outputPackage = options.outputPackage,
            codeGenerator = codeGenerator,
            fileSpec = fileSpec,
            fileName = options.outputClassName,
            sourceFiles = options.sourceFiles,
        )

        return CreateFileStep().process(options)
    }

    fun stepCreateMetaInfFile(): Boolean {
        val options = CreateMetaInfOptions(
            sourceFiles = options.sourceFiles,
            packageName = "META-INF.services",
            fileName = AergiaComponentRegistrationService::class.qualifiedName!!,
            serviceClassNameString = "${options.outputPackage}.${options.outputClassName}",
        )
        return CreateMetaInfStep(codeGenerator).process(options)
    }

}