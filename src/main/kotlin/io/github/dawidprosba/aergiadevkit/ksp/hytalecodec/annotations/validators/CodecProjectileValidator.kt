package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.validators

/**
 * Annotation to mark a field as a projectile, which will add a projectile validator to the codec.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class CodecProjectileValidator()

