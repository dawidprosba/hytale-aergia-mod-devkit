package io.github.dawidprosba.aergiadevkit.ksp.generator_pipeline

internal interface GeneratorPipelineStep<Input, Result> {
    fun process(input: Input ): Result
}