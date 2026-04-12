package io.github.dawidprosba.aergiadevkit.ksp.generation

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import io.github.dawidprosba.aergiadevkit.ksp.generator_pipeline.GeneratorPipeline

abstract class AbstractGenerator(val resolver: Resolver) : Generator {
    val pipeline = GeneratorPipeline(resolver)
    val sourceFiles : List<KSFile>? = null
    val deferredSymbols = mutableListOf<KSAnnotated>()
}