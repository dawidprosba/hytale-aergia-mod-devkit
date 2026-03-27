package io.github.dawidprosba.aergiadevkit.ksp.registry.processor.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.RegistryEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.COMPONENT_REGISTRY_PROXY_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.ENTITY_STORE_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.REGISTER_SYSTEM

class SystemRegistryGenerator(
    outputPackage: String,
    pluginClass: String,
    entries: List<RegistryEntryMetadata>,
    sourceFiles: Array<KSFile>,
    codeGenerator: CodeGenerator,
) : SimpleRegistryGenerator(outputPackage, pluginClass, entries, sourceFiles, codeGenerator) {
    override val objectName = "SystemRegistryGenerated"
    override val disabledLogMessage = "Skipping system '%s' (%s), reason -> disabled"
    override val registerMember: MemberName = REGISTER_SYSTEM
    override val registryParameterType: TypeName = COMPONENT_REGISTRY_PROXY_TYPE.parameterizedBy(ENTITY_STORE_TYPE)
}
