package io.github.dawidprosba.aergiadevkit.ksp.generation

import com.google.devtools.ksp.symbol.KSFile

interface PipelineProcessor {
    fun process() : Set<KSFile>
}