package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data

import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.CodeBlock

data class PropertyCodecMetadata(
    val annotation: KSAnnotation,
    val annotationArgs: Map<String, Any?>,
    val propertyName: String,
    val propertyQualifiedName: String?,
    val treatAsInherited: Boolean,
    val isRequired: Boolean,
    val codecSchemaType: CodeBlock,
    val codecKey: String
)
