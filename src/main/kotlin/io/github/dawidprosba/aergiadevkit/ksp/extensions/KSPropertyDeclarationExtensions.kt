package io.github.dawidprosba.aergiadevkit.ksp.extensions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

fun KSPropertyDeclaration.isInheritedProperty(holderClassDeclaration : KSClassDeclaration): Boolean {
    return this.parentDeclaration != holderClassDeclaration
}