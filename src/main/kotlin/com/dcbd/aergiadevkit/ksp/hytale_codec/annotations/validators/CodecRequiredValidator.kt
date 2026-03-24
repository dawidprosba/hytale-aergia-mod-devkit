package com.dcbd.aergiadevkit.ksp.hytale_codec.annotations.validators

/**
 * Annotation to mark a field as required, which will add a non-null validator to the codec.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class CodecRequiredValidator()
