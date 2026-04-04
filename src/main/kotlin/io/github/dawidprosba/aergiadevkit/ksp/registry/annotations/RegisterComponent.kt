package io.github.dawidprosba.aergiadevkit.ksp.registry.annotations

@Deprecated(
    message = "Use @HytaleComponent instead.",
    replaceWith = ReplaceWith("HytaleComponent(id, enabled)", "io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.HytaleComponent")
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterComponent(
    val id: String,
    val enabled: Boolean = true
)
