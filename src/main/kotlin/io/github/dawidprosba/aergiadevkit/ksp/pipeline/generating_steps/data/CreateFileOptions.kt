package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec

data class CreateFileOptions(
    val fileSpec: FileSpec,
    val codeGenerator: CodeGenerator,
    val sourceFiles: Array<KSFile>,
    val outputPackage: String,
    val fileName: String,
) {

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException("CreateFileOptions should not be compared for equality")
    }

    override fun hashCode(): Int {
        return 0
    }
}
