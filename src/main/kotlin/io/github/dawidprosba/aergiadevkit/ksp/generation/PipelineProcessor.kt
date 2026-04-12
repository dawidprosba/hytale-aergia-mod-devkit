package io.github.dawidprosba.aergiadevkit.ksp.generation

import com.google.devtools.ksp.symbol.KSAnnotated

interface PipelineProcessor {
    fun process() : Set<KSAnnotated>
}