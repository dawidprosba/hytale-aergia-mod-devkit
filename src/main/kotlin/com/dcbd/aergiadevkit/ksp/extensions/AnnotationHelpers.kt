package com.dcbd.aergiadevkit.ksp.extensions

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation

fun KSAnnotated.hasAnnotation(simpleName: String): Boolean {
    return annotations.any { it.shortName.asString() == simpleName }
}

fun KSAnnotated.getAnnotation(simpleName: String): KSAnnotation? {
    return annotations.firstOrNull { it.shortName.asString() == simpleName }
}

fun KSAnnotation.getArgs(): Map<String?, Any?> {
    return arguments.associate { it.name?.asString() to it.value }
}