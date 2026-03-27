package io.github.dawidprosba.aergiadevkit.ksp.registry.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterEvent(
    val eventClass: KClass<*>,
    val subject: KClass<*>,
    val enabled: Boolean = true
)
