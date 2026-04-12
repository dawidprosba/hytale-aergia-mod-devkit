package io.github.dawidprosba.aergiadevkit.ksp.pipeline

internal interface PipelineStep<Input, Result> {
    fun process(input: Input ): Result
}