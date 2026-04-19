package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.RegistryEntryMetadata

data class RegistryClassGenerationOptions(
    val pluginClassNameString: String,
    val generatedClassObjectName: String,
    val serviceRegistrationClassName: ClassName,
    val entries: List<RegistryEntryMetadata>,
    // Eg Component
    val elementClass: ClassName,
    // Eg EntityStore
    val elementClassT: ClassName,
    // Eg ComponentType (used as the map value type, may differ from elementClass)
    val mapValueClass: ClassName,
    val registerHelperFunctionMemberName: MemberName,
    val registryProxyType: ClassName,
)
