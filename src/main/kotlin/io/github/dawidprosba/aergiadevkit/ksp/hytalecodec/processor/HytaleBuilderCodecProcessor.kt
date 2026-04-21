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
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.processor.sub_processors.HytaleBuilderCodecPipelineProcessor

class HytaleBuilderCodecProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val processedClasses = mutableSetOf<String>()

    private var codecPipelineProcessor : HytaleBuilderCodecPipelineProcessor? = null

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val deferredSymbols = mutableListOf<KSAnnotated>()

        val processor = HytaleBuilderCodecPipelineProcessor(resolver)
        processor.process()
        deferredSymbols += processor.deferredSymbols
        codecPipelineProcessor = processor

        return deferredSymbols
    }

    override  fun finish() {

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