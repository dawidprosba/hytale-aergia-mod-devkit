package com.dcbd.hytale.ksp.registry.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterSystem(val enabled: Boolean = true)
