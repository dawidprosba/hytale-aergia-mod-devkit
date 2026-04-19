package io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.dawidprosba.aergiadevkit.ksp.extensions.findClassesWithAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.extensions.findPropertiesWithAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep
import kotlin.reflect.KClass

class FindClassesWithAnnotationHasPropertyAnnotated(
    private val resolver: Resolver,
    private val propertyAnnotationKClass: KClass<*>
) : PipelineStep<KClass<*>, List<KSClassDeclaration>> {
    /**
     * @param input - the kotlin class that is annotated
     */
    override fun process(input: KClass<*>): List<KSClassDeclaration> {
        return stepGetClassesWithAnnotation(input).filter {
            it.findPropertiesWithAnnotation(propertyAnnotationKClass.simpleName!!).isNotEmpty()
        }
    }

    private fun stepGetClassesWithAnnotation(annotationOnClass: KClass<*>): List<KSClassDeclaration> {
        return FindClassesWithAnnotation(resolver).process(input = annotationOnClass)
    }
}