package io.github.dawidprosba.aergiadevkit.ksp.extensions

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun Resolver.findClassesWithAnnotation(simpleName: String): Sequence<KSClassDeclaration> {
    return this
        .getSymbolsWithAnnotation(simpleName)
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.qualifiedName != null }
}