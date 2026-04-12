package io.github.dawidprosba.aergiadevkit.ksp.pipeline

class ChainedPipeline<T>(private var value: T) {

    fun <R> next(step: (T) -> R): ChainedPipeline<R> {
        return ChainedPipeline(step(value))
    }

    fun result(): T = value
}