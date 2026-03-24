package com.dcbd.hytale.ksp.registry.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterInteraction(val id: String, val enabled: Boolean = true)
