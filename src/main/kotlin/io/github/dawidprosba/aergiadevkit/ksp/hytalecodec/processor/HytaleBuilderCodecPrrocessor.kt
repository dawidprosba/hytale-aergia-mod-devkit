package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor

import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.GenerateCodec
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor.generators.CodecFileGenerator
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor.generators.CodecRegistryGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.dawidprosba.aergiadevkit.ksp.extensions.findClassesWithAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.extensions.findPropertiesWithAnnotation

class HytaleBuilderCodecPrrocessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val processedClasses = mutableSetOf<String>()
    private val collectedClassDeclarations = mutableListOf<KSClassDeclaration>()
    private val generateCodecAnnotationName = GenerateCodec::class.qualifiedName!!
    private val outputPackage = environment.options["registriesOutputPackage"]

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.findClassesWithAnnotation(generateCodecAnnotationName)
            .filterNot { it.qualifiedName?.asString() in processedClasses }
            .forEach { classDeclaration ->
                processClass(classDeclaration)
                collectedClassDeclarations.add(classDeclaration)
            }

        return emptyList()
    }

    override fun finish() {
        val outputPackage = outputPackage ?: return

        val sourceFiles = collectedClassDeclarations
            .mapNotNull { it.containingFile }
            .toTypedArray()

        CodecRegistryGenerator(
            outputPackage = outputPackage,
            classDeclarations = collectedClassDeclarations,
            sourceFiles = sourceFiles,
            codeGenerator = environment.codeGenerator,
        ).generate()
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

        processedClasses.add(classDeclaration.qualifiedName!!.asString())
    }
}