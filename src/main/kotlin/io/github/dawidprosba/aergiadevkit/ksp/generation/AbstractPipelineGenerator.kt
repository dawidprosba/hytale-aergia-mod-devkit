package io.github.dawidprosba.aergiadevkit.ksp.generation

import com.google.devtools.ksp.processing.CodeGenerator
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.ChainedPipeline

abstract class AbstractPipelineGenerator(val options : GeneratorOptions, val codeGenerator: CodeGenerator) : PipelineGenerator {
    val pipeline = ChainedPipeline(options)
}