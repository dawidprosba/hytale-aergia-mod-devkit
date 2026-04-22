package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific

import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.hytale.HYTALE_PROJECTILE_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.validators.CodecProjectileValidator

class AddProjectileValidatorStep(builder: CodeBlock.Builder) : AnnotationConditionalPropertyStep(builder) {

    override val annotationSimpleName: String = CodecProjectileValidator::class.simpleName!!

    override fun applyToBuilder(builder: CodeBlock.Builder) {
        builder.add(".addValidator(%T.VALIDATOR_CACHE.getValidator().late())\n", HYTALE_PROJECTILE_TYPE)
    }
}
