package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data

import com.google.devtools.ksp.symbol.KSFile

data class CreateMetaInfOptions(
    val sourceFiles: Array<KSFile>,
    val packageName: String,
    val fileName: String,
    val serviceClassNameString: String,
) {

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException("CreateMetaInfOptions should not be compared for equality")
    }

    override fun hashCode(): Int {
        return 0
    }
}