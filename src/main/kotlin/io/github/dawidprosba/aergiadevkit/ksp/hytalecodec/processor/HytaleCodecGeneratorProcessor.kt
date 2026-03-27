package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor

import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.GenerateCodec
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor.generators.CodecFileGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.dawidprosba.aergiadevkit.ksp.extensions.findClassesWithAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.extensions.findPropertiesWithAnnotation

class HytaleCodecGeneratorProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val processedClasses = mutableSetOf<String>()
    private val generateCodecAnnotationName = GenerateCodec::class.qualifiedName!!

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.findClassesWithAnnotation(generateCodecAnnotationName)
            .filterNot { it.simpleName.asString() in processedClasses }
            .forEach { classDeclaration ->
                processClass(classDeclaration)
            }

        return emptyList()
    }

    private fun processClass(classDeclaration: KSClassDeclaration) {
        val className = classDeclaration.simpleName.asString()
        environment.logger.info("Processing class: $className")

        val properties =
            classDeclaration.findPropertiesWithAnnotation(CodecProperty::class.simpleName!!)

        if (properties.isEmpty()) {
            environment.logger.warn(
                "No @CodecProperty found in $className, CODEC won't be generated, " +
                        "please add at least one property annotated " +
                        "with @CodecProperty or remove @GenerateCodec annotation"
            )
            return
        }

        CodecFileGenerator(
            classDeclaration = classDeclaration,
            properties = properties,
            codeGenerator = environment.codeGenerator
        ).generate()

        processedClasses.add(className)
    }
}