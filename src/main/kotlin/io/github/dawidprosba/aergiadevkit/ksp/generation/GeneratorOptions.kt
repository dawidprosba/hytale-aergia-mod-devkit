package io.github.dawidprosba.aergiadevkit.ksp.generation

import com.google.devtools.ksp.symbol.KSFile
import io.github.dawidprosba.aergiadevkit.ksp.registry.data.RegistryEntryMetadata

data class GeneratorOptions(
    val outputPackage: String,
    val pluginClass: String,
    val outputClassName: String,
    val entries: List<RegistryEntryMetadata>,
    val sourceFiles: Array<KSFile>
) {
    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException("GeneratorOptions should not be compared for equality")
    }

    override fun hashCode(): Int {
        return 0
    }
}
