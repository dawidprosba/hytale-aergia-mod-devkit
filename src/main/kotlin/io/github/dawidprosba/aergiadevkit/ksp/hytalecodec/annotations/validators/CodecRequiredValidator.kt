package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.validators

/**
 * Marks field as required in the codec.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class CodecRequiredValidator()
