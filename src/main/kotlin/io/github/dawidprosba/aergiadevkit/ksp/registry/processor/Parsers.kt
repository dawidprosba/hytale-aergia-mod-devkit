package io.github.dawidprosba.aergiadevkit.ksp.registry.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.Variance

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
    val companionDecl = parentDeclaration as? KSClassDeclaration
    val outerClass = companionDecl?.parentDeclaration as? KSClassDeclaration
    return outerClass?.qualifiedName?.asString()
        ?: companionDecl?.qualifiedName?.asString()
        ?: error("Cannot resolve containing class for function ${qualifiedName?.asString()}")
}

internal fun KSType.toKotlinString(): String {
    val name = declaration.qualifiedName?.asString() ?: return "*"
    if (arguments.isEmpty()) return name
    val args = arguments.joinToString(", ", transform = KSTypeArgument::toKotlinString)
    return "$name<$args>"
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
