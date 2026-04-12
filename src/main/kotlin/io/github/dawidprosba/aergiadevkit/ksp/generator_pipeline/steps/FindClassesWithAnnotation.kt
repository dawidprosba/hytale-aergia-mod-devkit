package io.github.dawidprosba.aergiadevkit.ksp.generator_pipeline.steps

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.dawidprosba.aergiadevkit.ksp.generator_pipeline.GeneratorPipelineStep
import kotlin.reflect.KClass

class FindClassesWithAnnotation(private val resolver: Resolver) : GeneratorPipelineStep<KClass<*>, List<KSClassDeclaration>>{
    override fun process(input: KClass<*>): List<KSClassDeclaration> {
        return resolver.getSymbolsWithAnnotation(input.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .toList()
    }

}