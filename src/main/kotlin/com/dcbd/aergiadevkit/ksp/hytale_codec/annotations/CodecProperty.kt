package com.dcbd.aergiadevkit.ksp.hytale_codec.annotations

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class CodecProperty(
    val documentation: String,
    val required: Boolean = true,
    val defaultValue: String = ""
)

