package io.github.dawidprosba.aergiadevkit.ksp.registry.processor.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.RegistryEntryMetadata
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.CODEC_MAP_REGISTRY_ASSETS_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.INTERACTION_TYPE
import io.github.dawidprosba.aergiadevkit.ksp.registry.processor.REGISTER_INTERACTION

class InteractionRegistryGenerator(
    outputPackage: String,
    pluginClass: String,
    entries: List<RegistryEntryMetadata>,
    sourceFiles: Array<KSFile>,
    codeGenerator: CodeGenerator,
) : SimpleRegistryGenerator(outputPackage, pluginClass, entries, sourceFiles, codeGenerator) {
    override val objectName = "InteractionRegistryGenerated"
    override val disabledLogMessage = "Skipping interaction '%s' (%s), reason -> disabled"
    override val registerMember: MemberName = REGISTER_INTERACTION
    override val registryParameterType: TypeName = CODEC_MAP_REGISTRY_ASSETS_TYPE.parameterizedBy(INTERACTION_TYPE, STAR)
}
