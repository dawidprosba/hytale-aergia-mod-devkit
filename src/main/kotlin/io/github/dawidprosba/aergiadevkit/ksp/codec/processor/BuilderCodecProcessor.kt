package io.github.dawidprosba.aergiadevkit.ksp.codec.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier

class BuilderCodecProcessor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private val codeGenerator: CodeGenerator = environment.codeGenerator
    private val logger: KSPLogger = environment.logger
    private val generateAnnotation =
        "io.github.dawidprosba.aergiadevkit.ksp.codec.annotations.GenerateBuilderCodec"
    private val fieldAnnotation = "io.github.dawidprosba.aergiadevkit.ksp.codec.annotations.CodecField"
    private val nestedAnnotation = "io.github.dawidprosba.aergiadevkit.ksp.codec.annotations.CodecNested"
    private val generatedFiles = mutableSetOf<String>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(generateAnnotation)

        symbols.forEach { symbol ->
            val classDeclaration = symbol as? KSClassDeclaration
            if (classDeclaration == null) {
                logger.error("@GenerateBuilderCodec can only target classes.", symbol)
                return@forEach
            }

            generateCodecFor(classDeclaration)
        }

        return emptyList()
    }

    private fun generateCodecFor(classDeclaration: KSClassDeclaration) {
        val packageName = classDeclaration.packageName.asString()
        val className = classDeclaration.simpleName.asString()
        val generatedName = "${className}GeneratedCodec"
        val generatedFqcn = "$packageName.$generatedName"

        if (!generatedFiles.add(generatedFqcn)) {
            return
        }

        val ctorParameters = classDeclaration.primaryConstructor?.parameters.orEmpty()
        val parentCodecExpression = resolveParentCodecExpression(classDeclaration)

        val constructorScalarFields = ctorParameters.mapNotNull { parameter ->
            val fieldMetadata = parameter.annotations.firstOrNull {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == fieldAnnotation
            } ?: return@mapNotNull null

            val fieldName = parameter.name?.asString()
            if (fieldName == null) {
                logger.error("All @CodecField constructor parameters must be named.", parameter)
                return@mapNotNull null
            }

            parseScalarField(parameter, fieldName, fieldMetadata.arguments.associate { arg ->
                arg.name?.asString().orEmpty() to arg.value
            })
        }

        if (constructorScalarFields.isNotEmpty() && constructorScalarFields.size != ctorParameters.size) {
            logger.error(
                "$className must annotate every primary constructor parameter with @CodecField.",
                classDeclaration
            )
            return
        }

        val constructorScalarFieldNames = constructorScalarFields.map { it.name }.toSet()

        val classProperties = classDeclaration.getAllProperties()
            .filter { it.parentDeclaration == classDeclaration }
            .toList()

        val propertyScalarFields = classProperties.mapNotNull { property ->
            val propertyName = property.simpleName.asString()
            if (propertyName in constructorScalarFieldNames) {
                return@mapNotNull null
            }

            val fieldMetadata = property.annotations.firstOrNull {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == fieldAnnotation
            } ?: return@mapNotNull null

            val hasNestedAnnotation = property.annotations.any {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == nestedAnnotation
            }
            if (hasNestedAnnotation) {
                logger.error("'${propertyName}' cannot use both @CodecField and @CodecNested.", property)
                return@mapNotNull null
            }

            if (!property.isMutable) {
                logger.error("@CodecField property '${propertyName}' must be a mutable var.", property)
                return@mapNotNull null
            }

            if (Modifier.PRIVATE in property.modifiers || Modifier.PROTECTED in property.modifiers) {
                logger.error(
                    "@CodecField property '${propertyName}' must not be private/protected for generated codec access.",
                    property
                )
                return@mapNotNull null
            }

            parseScalarField(property, propertyName, fieldMetadata.arguments.associate { arg ->
                arg.name?.asString().orEmpty() to arg.value
            })
        }

        val duplicateFieldNames = (constructorScalarFields + propertyScalarFields)
            .groupBy { it.name }
            .filterValues { it.size > 1 }
            .keys
        if (duplicateFieldNames.isNotEmpty()) {
            logger.error(
                "$className has duplicate @CodecField declarations for: ${duplicateFieldNames.joinToString(", ")}.",
                classDeclaration
            )
            return
        }

        val scalarFields = constructorScalarFields + propertyScalarFields

        val duplicateCodecKeys = scalarFields
            .groupBy { it.key }
            .filterValues { it.size > 1 }
            .keys
        if (duplicateCodecKeys.isNotEmpty()) {
            logger.error(
                "$className has duplicate codec keys in @CodecField: ${duplicateCodecKeys.joinToString(", ")}.",
                classDeclaration
            )
            return
        }

        val nestedFields = classProperties
            .mapNotNull { property ->
                val nestedMetadata = property.annotations.firstOrNull {
                    it.annotationType.resolve().declaration.qualifiedName?.asString() == nestedAnnotation
                } ?: return@mapNotNull null

                parseNestedField(
                    property,
                    nestedMetadata.arguments.associate { arg ->
                        arg.name?.asString().orEmpty() to arg.value
                    }
                )
            }
            .toList()

        if (scalarFields.isEmpty() && nestedFields.isEmpty() && parentCodecExpression == null) {
            logger.error(
                "$className has no codec fields and no parent codec. Add @CodecField/@CodecNested or define parent CODEC/codec.",
                classDeclaration
            )
            return
        }

        val constructorCall = if (constructorScalarFields.isNotEmpty()) {
            val constructorDefaults =
                constructorScalarFields.joinToString(",\n                            ") { it.defaultLiteral }
            "$className(\n                            $constructorDefaults\n                        )"
        } else {
            "$className()"
        }

        val builderCall = if (parentCodecExpression != null) {
            """
                .builder($className::class.java, {
                    $constructorCall
                }, $parentCodecExpression)
            """.trimIndent()
        } else {
            """
                .builder($className::class.java) {
                    $constructorCall
                }
            """.trimIndent()
        }

        val builderAppends = buildString {
            val usesParentCodec = parentCodecExpression != null
            (scalarFields + nestedFields).forEach { field ->
                if (usesParentCodec) {
                    append(
"""
                    .appendInherited(
                        KeyedCodec("${field.key}", ${field.codecExpression}, ${field.required}),
                        { instance: $className, value: ${field.valueType} -> instance.${field.name} = value },
                        { instance: $className -> instance.${field.name} },
                        { instance: $className, parent: $className -> instance.${field.name} = parent.${field.name} }
                    )
"""
                    )
                } else {
                    append(
"""
                    .append(
                        KeyedCodec("${field.key}", ${field.codecExpression}, ${field.required}),
                        { instance: $className, value: ${field.valueType} -> instance.${field.name} = value },
                        { instance: $className -> instance.${field.name} }
                    )
"""
                    )
                }
                if (field.documentation.isNotBlank()) {
                    append("                    .documentation(${field.documentation.quoted()})")
                }
                append("\n")
                append("                    .add()")
            }
        }

        val fileContent =
            """
            package $packageName

            import io.github.dawidprosba.aergiadevkit.ksp.hytale_codec.api.CodecProvider
            import com.hypixel.hytale.codec.Codec
            import com.hypixel.hytale.codec.KeyedCodec
            import com.hypixel.hytale.codec.builder.BuilderCodec

            object $generatedName : CodecProvider<$className> {
                override val CODEC: BuilderCodec<$className> = BuilderCodec
                    $builderCall
                    $builderAppends
                    .build()
            }
            """.trimIndent()

        val output = codeGenerator.createNewFile(
            Dependencies(false, classDeclaration.containingFile!!),
            packageName,
            generatedName
        )

        output.bufferedWriter().use { it.write(fileContent) }
    }

    private fun resolveParentCodecExpression(classDeclaration: KSClassDeclaration): String? {
        val parent = classDeclaration.superTypes
            .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
            .firstOrNull { declaration ->
                declaration.classKind != com.google.devtools.ksp.symbol.ClassKind.INTERFACE &&
                        declaration.qualifiedName?.asString() != "kotlin.Any"
            } ?: return null

        val parentQualifiedName = parent.qualifiedName?.asString() ?: return null

        val parentCodecField = findDeclaredProperty(parent, "CODEC")
        if (parentCodecField != null && isBuilderCodecType(parentCodecField.type.resolve())) {
            logger.info("Detected parent codec field CODEC on $parentQualifiedName")
            return "$parentQualifiedName.CODEC"
        }

        val parentCompanionCodecField = findCompanionProperty(parent, "CODEC")
        if (parentCompanionCodecField != null && isBuilderCodecType(parentCompanionCodecField.type.resolve())) {
            logger.info("Detected parent companion codec field CODEC on $parentQualifiedName")
            return "$parentQualifiedName.CODEC"
        }

        val parentCodecProperty = findCompanionProperty(parent, "codec")
        if (parentCodecProperty != null && isBuilderCodecType(parentCodecProperty.type.resolve())) {
            logger.info("Detected parent codec property codec on $parentQualifiedName")
            return "$parentQualifiedName.codec"
        }

        logger.warn(
            "${classDeclaration.simpleName.asString()} extends $parentQualifiedName but no parent CODEC/codec field was found."
        )
        return null
    }

    private fun findDeclaredProperty(
        classDeclaration: KSClassDeclaration,
        name: String
    ): KSPropertyDeclaration? =
        classDeclaration.declarations
            .filterIsInstance<KSPropertyDeclaration>()
            .firstOrNull { it.simpleName.asString() == name }

    private fun findCompanionProperty(
        classDeclaration: KSClassDeclaration,
        name: String
    ): KSPropertyDeclaration? {
        val companion = classDeclaration.declarations
            .filterIsInstance<KSClassDeclaration>()
            .firstOrNull { it.isCompanionObject }
            ?: return null

        return companion.declarations
            .filterIsInstance<KSPropertyDeclaration>()
            .firstOrNull { it.simpleName.asString() == name }
    }

    private fun isBuilderCodecType(type: KSType): Boolean =
        type.declaration.qualifiedName?.asString() == "com.hypixel.hytale.codec.builder.BuilderCodec"

    private fun parseScalarField(
        declaration: KSNode,
        name: String,
        args: Map<String, Any?>
    ): FieldSpec {
        val kind = args["kind"]?.toString()?.substringAfterLast('.') ?: run {
            logger.error("@CodecField(kind=...) is required on '$name'.", declaration)
            return FieldSpec.invalid(name)
        }

        val defaultValue = args["defaultValue"] as? String ?: run {
            logger.error("@CodecField(defaultValue=...) is required on '$name'.", declaration)
            return FieldSpec.invalid(name)
        }

        val key = args["key"] as? String ?: name
        val required = args["required"] as? Boolean ?: true
        val documentation = args["documentation"] as? String ?: ""

        val mapping = when (kind) {
            "BOOLEAN" -> "BOOLEAN" to "Boolean"
            "DOUBLE" -> "DOUBLE" to "Double"
            "FLOAT" -> "FLOAT" to "Float"
            "INT" -> "INT" to "Int"
            "LONG" -> "LONG" to "Long"
            "STRING" -> "STRING" to "String"
            else -> {
                logger.error("Unsupported CodecKind '$kind' on '$name'.", declaration)
                "STRING" to "String"
            }
        }

        val literal = toLiteral(defaultValue, mapping.second, name, declaration)

        return FieldSpec(
            name = name,
            key = key,
            required = required,
            documentation = documentation,
            codecExpression = "Codec.${mapping.first}",
            valueType = mapping.second,
            defaultLiteral = literal
        )
    }

    private fun parseNestedField(
        property: KSPropertyDeclaration,
        args: Map<String, Any?>
    ): FieldSpec? {
        val name = property.simpleName.asString()
        val key = args["key"] as? String ?: name
        val required = args["required"] as? Boolean ?: true
        val documentation = args["documentation"] as? String ?: ""

        val providerType = args["codecProvider"] as? KSType
        if (providerType == null) {
            logger.error("@CodecNested(codecProvider=...) is required on '$name'.", property)
            return null
        }

        val providerQualifiedName = providerType.declaration.qualifiedName?.asString()
        if (providerQualifiedName == null) {
            logger.error("Unable to resolve codecProvider on '$name'.", property)
            return null
        }

        val resolvedType = property.type.resolve()
        val valueType = buildString {
            append(resolvedType.declaration.qualifiedName?.asString() ?: resolvedType.toString())
            if (resolvedType.isMarkedNullable) {
                append("?")
            }
        }

        // Nested fields are assigned post-construction, so defaults are not needed.
        return FieldSpec(
            name = name,
            key = key,
            required = required,
            documentation = documentation,
            codecExpression = "${providerQualifiedName}GeneratedCodec.codec",
            valueType = valueType,
            defaultLiteral = "",
        )
    }

    private fun toLiteral(
        raw: String,
        type: String,
        fieldName: String,
        declaration: KSNode
    ): String {
        return when (type) {
            "Boolean" -> if (raw == "true" || raw == "false") raw else {
                logger.error(
                    "defaultValue for '$fieldName' must be 'true' or 'false'.",
                    declaration
                )
                "false"
            }

            "Double" -> raw.toDoubleOrNull()?.toString() ?: run {
                logger.error("defaultValue for '$fieldName' must be a Double.", declaration)
                "0.0"
            }

            "Float" -> raw.toFloatOrNull()?.let { "${it}f" } ?: run {
                logger.error("defaultValue for '$fieldName' must be a Float.", declaration)
                "0.0f"
            }

            "Int" -> raw.toIntOrNull()?.toString() ?: run {
                logger.error("defaultValue for '$fieldName' must be an Int.", declaration)
                "0"
            }

            "Long" -> raw.toLongOrNull()?.let { "${it}L" } ?: run {
                logger.error("defaultValue for '$fieldName' must be a Long.", declaration)
                "0L"
            }

            "String" -> raw.quoted()
            else -> raw.quoted()
        }
    }

    private data class FieldSpec(
        val name: String,
        val key: String,
        val required: Boolean,
        val documentation: String,
        val codecExpression: String,
        val valueType: String,
        val defaultLiteral: String
    ) {
        companion object {
            fun invalid(name: String): FieldSpec = FieldSpec(
                name = name,
                key = name,
                required = true,
                documentation = "",
                codecExpression = "Codec.STRING",
                valueType = "String",
                defaultLiteral = "\"\""
            )
        }
    }

    private fun String.quoted(): String = "\"" +
            this.replace("\\", "\\\\").replace("\"", "\\\"") +
            "\""
}

