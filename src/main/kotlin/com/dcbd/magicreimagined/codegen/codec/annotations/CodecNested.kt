package com.dcbd.hytale.ksp.codec.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class CodecNested(
    val key: String,
    val codecProvider: KClass<*>,
    val required: Boolean = true,
    val documentation: String = ""
)

