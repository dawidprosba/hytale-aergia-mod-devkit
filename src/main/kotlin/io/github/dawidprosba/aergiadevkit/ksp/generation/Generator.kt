package io.github.dawidprosba.aergiadevkit.ksp.generation

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated

interface Generator {
    /**
     * @return deferred symbols
     */
    fun process() : Set<KSAnnotated>
}