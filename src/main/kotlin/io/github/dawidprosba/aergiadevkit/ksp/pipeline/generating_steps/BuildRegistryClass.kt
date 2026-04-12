package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.RegistryClassGenerationOptions
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.RegistryEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.HYTALE_LOGGER_TYPE

class BuildRegistryClass : PipelineStep<RegistryClassGenerationOptions, TypeSpec> {
    override fun process(input: RegistryClassGenerationOptions): TypeSpec {
        val pluginClassName = ClassName.bestGuess(input.pluginClassNameString)

        val loggerProperty = PropertySpec.builder("LOGGER", HYTALE_LOGGER_TYPE)
            .initializer("%T.get(%T::class.simpleName)", HYTALE_LOGGER_TYPE, pluginClassName)
            .build()

        val companion = TypeSpec.companionObjectBuilder()
            .addProperty(loggerProperty)
            .build()

        val registerAllFunction = buildRegisterAllFunction(
            input.entries,
            input.elementClass,
            input.elementClassT,
            input.mapValueClass,
            input.registryProxyType,
            input.registerHelperFunctionMemberName,
        )
        return TypeSpec.classBuilder(input.generatedClassObjectName)
            .addSuperinterface(input.serviceRegistrationClassName)
            .addType(companion)
            .addFunction(registerAllFunction)
            .build()
    }

    private fun buildRegisterAllFunction(
        entries: List<RegistryEntryMetadata>,
        elementClass: ClassName,
        elementT: ClassName,
        mapValueClass: ClassName,
        registryProxyType: ClassName,
        registerHelperFunctionMemberName: MemberName
    ): FunSpec {
        val outComponent = WildcardTypeName.producerOf(elementClass.parameterizedBy(elementT))
        val entryClass = ClassName("java.lang", "Class").parameterizedBy(outComponent)
        val entryClassT = mapValueClass.parameterizedBy(elementT, STAR)
        val returnType = ClassName("kotlin.collections", "MutableMap").parameterizedBy(
            entryClass,
            entryClassT
        )
        val body = buildRegisterAllBody(
            entries,
            entryClass,
            entryClassT,
            registerHelperFunctionMemberName

        )
        return FunSpec.builder("registerAll")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("registry", registryProxyType.parameterizedBy(elementT))
            .returns(returnType)
            .addCode(body)
            .build()
    }

    private fun buildRegisterAllBody(
        entries: List<RegistryEntryMetadata>,
        entryClass: TypeName,
        entryClassT: TypeName,
        registerHelperFunctionMemberName: MemberName
    ): CodeBlock {
        return CodeBlock.builder().apply {
            addStatement("val result = mutableMapOf<%T, %T>()", entryClass, entryClassT)
            entries.forEach { entry ->
                val entryClass = ClassName.bestGuess(entry.qualifiedName)
                if (!entry.enabled) {
                    addStatement(
                        "LOGGER.atWarning().log(%S, %S)",
                        "Skipping component '%s' (%s), reason -> disabled",
                        entry.qualifiedName,
                    )
                } else {
                    addStatement(
                        "result[%T::class.java] = %M(%T::class, registry)",
                        entryClass,
                        registerHelperFunctionMemberName,
                        entryClass,
                    )
                }
            }
            addStatement("return result")
        }.build()
    }
}