package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific

import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.hytale.HYTALE_VALIDATORS_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.validators.CodecRequiredValidator

class AddRequiredValidatorStep(builder: CodeBlock.Builder) : AnnotationConditionalPropertyStep(builder) {

    override val annotationSimpleName: String = CodecRequiredValidator::class.simpleName!!

    override fun applyToBuilder(builder: CodeBlock.Builder) {
        builder.add(".addValidator(%T.nonNull())\n", HYTALE_VALIDATORS_TYPE)
    }
}
