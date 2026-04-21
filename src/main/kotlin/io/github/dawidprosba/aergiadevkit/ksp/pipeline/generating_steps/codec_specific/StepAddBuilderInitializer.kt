package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.extensions.getCompanionPropertiesFromParentClass
import io.github.dawidprosba.aergiadevkit.ksp.hytale.HYTALE_BUILDER_CODEC_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStepWithoutParam

class StepAddBuilderInitializer(
    private val options : BuilderInitializerOptions
) : PipelineStepWithoutParam<CodeBlock.Builder> {
    data class BuilderInitializerOptions(
        val classDeclaration: KSClassDeclaration,
        /**
         * Name of property that the generator looks up in companion object of parent class.
         */
        val expectedCodecProperty : String = "CODEC",
        val hytaleBuilderCodecType: ClassName = HYTALE_BUILDER_CODEC_TYPE,
    )

    private val parentCodecProperties = options.classDeclaration.getCompanionPropertiesFromParentClass(
        options.expectedCodecProperty
    )
    private val codecFromParent = parentCodecProperties[options.expectedCodecProperty]
    private val className = options.classDeclaration.simpleName.asString()
    private val classTypeName = ClassName(options.classDeclaration.packageName.asString(), className)
    override fun process(): CodeBlock.Builder {
        val codeBlockBuilder = CodeBlock.builder()

        if(codecFromParent != null) {
             addInitializerWithParent(codeBlockBuilder)
        } else {
            addInitializerWithParent(codeBlockBuilder)
        }
        return codeBlockBuilder;
    }

    private fun addInitializerWithoutParent(codeBlockBuilder: CodeBlock.Builder) {
        codeBlockBuilder.add(
            "%T.builder(%T::class.java, ::%T)\n",
            options.hytaleBuilderCodecType,
            classTypeName,
            classTypeName
        )
    }

    private fun addInitializerWithParent(codeBlockBuilder: CodeBlock.Builder) {
        codeBlockBuilder.add(
            "%T.builder(%T::class.java, ::%T, %L)\n",
            HYTALE_BUILDER_CODEC_TYPE,
            classTypeName,
            classTypeName,
            codecFromParent
        )
    }
}