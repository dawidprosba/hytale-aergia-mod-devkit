package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations

/**
 * Marks a lateinit property in a companion object to be populated automatically
 * by CodecRegistry during initialization.
 *
 * Usage:
 * ```
 * companion object {
 *     @InjectCodec
 *     lateinit var CODEC: BuilderCodec<MyClass>
 * }
 * ```
 *
 * No interface implementation or manual reference to the generated codec is required.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectCodec
