package io.github.dawidprosba.aergiadevkit.ksp.registry.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HytaleComponent(
    val id: String,
    val enabled: Boolean = true
)
