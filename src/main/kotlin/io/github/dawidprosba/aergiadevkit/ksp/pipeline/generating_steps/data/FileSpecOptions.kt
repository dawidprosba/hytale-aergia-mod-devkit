package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data

import com.squareup.kotlinpoet.TypeSpec

data class FileSpecOptions(val outputPackage : String, val outputClassName : String, val content : TypeSpec)
