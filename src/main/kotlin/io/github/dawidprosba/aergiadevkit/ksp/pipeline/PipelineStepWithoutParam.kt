package io.github.dawidprosba.aergiadevkit.ksp.pipeline

internal interface PipelineStepWithoutParam<Result> {
    fun process(): Result
}