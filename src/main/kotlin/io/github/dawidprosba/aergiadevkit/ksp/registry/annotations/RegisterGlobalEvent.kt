package io.github.dawidprosba.aergiadevkit.ksp.registry.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterGlobalEvent(
    val eventClass: KClass<*>,
    val enabled: Boolean = true
)
