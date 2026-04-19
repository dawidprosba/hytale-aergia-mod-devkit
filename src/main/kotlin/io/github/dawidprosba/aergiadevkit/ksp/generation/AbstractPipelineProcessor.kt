package io.github.dawidprosba.aergiadevkit.ksp.generation

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.ChainedPipeline

abstract class AbstractPipelineProcessor(val resolver: Resolver) : PipelineProcessor {
    val pipeline = ChainedPipeline(resolver)
    val deferredSymbols = mutableSetOf<KSAnnotated>()
}