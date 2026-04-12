package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.CreateMetaInfOptions

class CreateMetaInfStep(val codeGenerator: CodeGenerator) : PipelineStep<CreateMetaInfOptions, Boolean> {
    override fun process(input: CreateMetaInfOptions): Boolean {
        try {
            codeGenerator.createNewFile(
                dependencies = Dependencies(true, *input.sourceFiles),
                packageName = input.packageName,
                fileName = input.fileName,
                extensionName = "",
            ).use { stream ->
                stream.write(input.serviceClassNameString.toByteArray())
            }
        } catch (e: FileAlreadyExistsException) {
            return false
        }

        return true
    }

}