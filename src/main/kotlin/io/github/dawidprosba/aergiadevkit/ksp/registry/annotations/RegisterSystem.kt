package io.github.dawidprosba.aergiadevkit.ksp.registry.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterSystem(val enabled: Boolean = true)
