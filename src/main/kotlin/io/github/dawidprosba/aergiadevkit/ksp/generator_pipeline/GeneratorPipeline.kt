package io.github.dawidprosba.aergiadevkit.ksp.generator_pipeline

class GeneratorPipeline<T>(private var value: T) {

    fun <R> next(step: (T) -> R): GeneratorPipeline<R> {
        return GeneratorPipeline(step(value))
    }

    fun result(): T = value
}