package com.dcbd.hytale.ksp.registry.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration

fun parseQualifiedName(classDeclaration: KSClassDeclaration): String {
    val qualifiedName = classDeclaration.qualifiedName?.asString()
        ?: throw IllegalStateException("Unable to resolve qualified name for interaction class.")

    return qualifiedName
}

fun getAnnotationArguments(classDeclaration: KSClassDeclaration, annotationName: String): Map<String, Any?> {
    val annotation = classDeclaration.annotations.firstOrNull {
        it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationName
    }

    return annotation?.arguments?.associate { arg ->
        arg.name?.asString().orEmpty() to arg.value
    }.orEmpty()
}