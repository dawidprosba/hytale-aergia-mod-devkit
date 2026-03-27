package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class HytaleBuilderCodecProcessorProvider : SymbolProcessorProvider{
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return HytaleCodecGeneratorProcessor(environment)
    }
}