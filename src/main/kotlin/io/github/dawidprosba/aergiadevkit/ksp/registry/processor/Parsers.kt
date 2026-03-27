package io.github.dawidprosba.aergiadevkit.ksp.registry.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Variance

internal data class RegisterGlobalEventArgs(
    val eventClassQualifiedName: String,
    val enabled: Boolean,
) {
    companion object {
        fun from(annotationArgs: Map<String, Any?>, qualifiedName: String) = RegisterGlobalEventArgs(
            eventClassQualifiedName = (annotationArgs["eventClass"] as? KSType)
                ?.declaration?.qualifiedName?.asString()
                ?: error("Missing eventClass for @RegisterGlobalEvent on $qualifiedName"),
            enabled = annotationArgs.getOrDefault("enabled", true) as Boolean,
        )
    }
}

internal data class RegisterEventArgs(
    val eventClassQualifiedName: String,
    val subjectClassQualifiedName: String,
    val enabled: Boolean,
) {
    companion object {
        fun from(annotationArgs: Map<String, Any?>, qualifiedName: String) = RegisterEventArgs(
            eventClassQualifiedName = (annotationArgs["eventClass"] as? KSType)
                ?.declaration?.qualifiedName?.asString()
                ?: error("Missing eventClass for @RegisterEvent on $qualifiedName"),
            subjectClassQualifiedName = (annotationArgs["subject"] as? KSType)
                ?.declaration?.qualifiedName?.asString()
                ?: error("Missing subject for @RegisterEvent on $qualifiedName"),
            enabled = annotationArgs.getOrDefault("enabled", true) as Boolean,
        )
    }
}

internal fun KSClassDeclaration.qualifiedNameString(): String =
    qualifiedName?.asString()
        ?: throw IllegalStateException("Unable to resolve qualified name for class ${simpleName.asString()}.")

internal fun KSClassDeclaration.annotationArguments(annotationName: String): Map<String, Any?> =
    annotations.firstOrNull {
        it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationName
    }?.arguments?.associate { it.name?.asString().orEmpty() to it.value }.orEmpty()

internal fun KSFunctionDeclaration.annotationArguments(annotationName: String): Map<String, Any?> =
    annotations.first {
        it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationName
    }.arguments.associate { it.name?.asString().orEmpty() to it.value }

internal fun KSFunctionDeclaration.containingClassQualifiedName(): String {
    val companionObject = parentDeclaration as? KSClassDeclaration
    val outerClass = companionObject?.parentDeclaration as? KSClassDeclaration
    return outerClass?.qualifiedName?.asString()
        ?: companionObject?.qualifiedName?.asString()
        ?: error("Cannot resolve containing class for function ${qualifiedName?.asString()}")
}

internal fun KSType.toKotlinString(): String {
    if (declaration is KSTypeParameter) {
        val upperBound = (declaration as KSTypeParameter).bounds.firstOrNull()?.resolve()
        return upperBound?.toKotlinString() ?: "kotlin.Any"
    }
    val name = declaration.qualifiedName?.asString() ?: return "kotlin.Any"
    if (arguments.isEmpty()) return name
    val typeArguments = arguments.joinToString(", ", transform = KSTypeArgument::toKotlinString)
    return "$name<$typeArguments>"
}

private fun KSTypeArgument.toKotlinString(): String {
    if (variance == Variance.STAR) return "*"
    val resolved = type?.resolve() ?: return "*"
    return when (variance) {
        Variance.COVARIANT -> "out ${resolved.toKotlinString()}"
        Variance.CONTRAVARIANT -> "in ${resolved.toKotlinString()}"
        else -> resolved.toKotlinString()
    }
}
