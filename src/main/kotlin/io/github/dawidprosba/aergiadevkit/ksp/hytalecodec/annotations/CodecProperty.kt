package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class CodecProperty(
    val documentation: String,
    val required: Boolean = true
)

