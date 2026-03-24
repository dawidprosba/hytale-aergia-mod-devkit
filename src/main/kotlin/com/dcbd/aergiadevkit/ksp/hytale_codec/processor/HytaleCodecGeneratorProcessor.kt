package com.dcbd.aergiadevkit.ksp.hytale_codec.processor

import com.dcbd.aergiadevkit.ksp.extensions.hasAnnotation
import com.dcbd.aergiadevkit.ksp.hytale_codec.annotations.CodecProperty
import com.dcbd.aergiadevkit.ksp.hytale_codec.annotations.GenerateCodec
import com.dcbd.aergiadevkit.ksp.hytale_codec.processor.strategy.CodecFileGeneratorStrategy
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

class HytaleCodecGeneratorProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private val processedClasses = mutableSetOf<String>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        findCodecGeneratorClasses(resolver)
            .filter { processedClasses.add(it.qualifiedName?.asString() ?: return@filter false) }
            .forEach { clazz -> processClass(clazz) }
        return emptyList()
    }

    private fun processClass(classDeclaration: KSClassDeclaration) {
        val className = classDeclaration.simpleName.asString()
        environment.logger.warn("Processing class: $className")

        val properties = findCodecProperties(classDeclaration)
        if (properties.isEmpty()) {
            environment.logger.warn("No @CodecProperty found in $className, skipping.")
            return
        }

        CodecFileGeneratorStrategy(
            classDeclaration = classDeclaration,
            properties = properties,
            codeGenerator = environment.codeGenerator
        ).generate()
    }

    // --- Finders ---

    private fun findCodecGeneratorClasses(resolver: Resolver): Sequence<KSClassDeclaration> {
        return resolver
            .getSymbolsWithAnnotation(GenerateCodec::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
    }

    private fun findCodecProperties(clazz: KSClassDeclaration): List<KSPropertyDeclaration> {
        return clazz.getAllProperties()
            .filter { it.hasAnnotation(CodecProperty::class.simpleName!!) }
            .toList()
    }
}