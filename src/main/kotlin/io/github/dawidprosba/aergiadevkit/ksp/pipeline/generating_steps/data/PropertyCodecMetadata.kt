package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.CodeBlock
import io.github.dawidprosba.aergiadevkit.ksp.extensions.getAnnotation
import io.github.dawidprosba.aergiadevkit.ksp.extensions.getArgs
import io.github.dawidprosba.aergiadevkit.ksp.extensions.hasAnyCompanionProperty
import io.github.dawidprosba.aergiadevkit.ksp.extensions.isInheritedProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.codec_specific.ResolveCodecSchemaType

data class PropertyCodecMetadata(
    val propertyDeclaration : KSPropertyDeclaration,
    val annotation: KSAnnotation,
    val annotationArgs: Map<String, Any?>,
    val propertyName: String,
    val propertyQualifiedName: String?,
    val treatAsInherited: Boolean,
    val isRequired: Boolean,
    val codecSchemaType: CodeBlock,
    val codecKey: String
) {
    companion object {
        fun from(
            propertyDeclaration: KSPropertyDeclaration,
            classDeclaration: KSClassDeclaration,
            expectedCodecProperty: String
        ): PropertyCodecMetadata {
            val annotation =
                propertyDeclaration.getAnnotation(CodecProperty::class.simpleName!!)
            val annotationArgs = annotation?.getArgs() ?: emptyMap()
            val propertyName = propertyDeclaration.simpleName.asString()
            val propertyType = propertyDeclaration.type.resolve()
            val propertyQualifiedName = propertyType.declaration.qualifiedName?.asString()
            val hasCodecProperty =
                (propertyDeclaration.type.resolve().declaration as? KSClassDeclaration)
                    ?.hasAnyCompanionProperty(expectedCodecProperty)
                    ?: false
            val treatAsInherited =
                propertyDeclaration.isInheritedProperty(classDeclaration) || hasCodecProperty
            val isRequired = annotationArgs.getOrDefault("isRequired", true) as Boolean
            val codecSchemaType = ResolveCodecSchemaType().process(
                ResolveCodecSchemaType.Input(
                    propertyQualifiedName = propertyQualifiedName ?: "",
                    propertyType = propertyType
                )
            )
            val codecKey = propertyName.replaceFirstChar { it.uppercase() }

            return PropertyCodecMetadata(
                annotation = annotation!!,
                annotationArgs = annotationArgs,
                propertyName = propertyName,
                propertyQualifiedName = propertyQualifiedName,
                treatAsInherited = treatAsInherited,
                isRequired = isRequired,
                codecSchemaType = codecSchemaType,
                codecKey = codecKey
            )
        }
    }
}