package com.dcbd.aergiadevkit.ksp.codec.annotations

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class CodecField(
    val key: String,
    val kind: CodecKind,
    val required: Boolean = true,
    val defaultValue: String,
    val documentation: String = ""
)

