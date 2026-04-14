package io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep

class ValidateAnnotationOnCorrectTarget(private val allowedTargets: List<String>) : PipelineStep<List<KSClassDeclaration>, List<KSAnnotated>> {

    override fun process(input: List<KSClassDeclaration>): List<KSAnnotated> {
        val invalidTargets = input
            .filterIsInstance<KSClassDeclaration>()
            .filter { kClass ->
                allowedTargets.none { qualifiedName -> isSubtype(kClass, qualifiedName, depth = 3) }
            }

        if (invalidTargets.isNotEmpty()) {
            throw IllegalArgumentException(
                "Annotation applied to invalid targets: ${invalidTargets.map { it.qualifiedName?.asString() }}. " +
                        "Allowed targets must extend one of: ${allowedTargets.map { it.substringAfterLast('.') }}"
            )
        }

        return input
    }

    private fun isSubtype(kClass: KSClassDeclaration, targetName: String, depth: Int): Boolean {
        if (depth == 0) return false
        return kClass.superTypes.any { superType ->
            val resolved = superType.resolve().declaration
            if (resolved.qualifiedName?.asString() == targetName) {
                true
            } else {
                (resolved as? KSClassDeclaration)?.let { isSubtype(it, targetName, depth - 1) } ?: false
            }
        }
    }

}