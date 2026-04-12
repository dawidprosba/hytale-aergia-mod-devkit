package io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.generating_steps.data.CreateFileOptions

class CreateFileStep : PipelineStep<CreateFileOptions, Boolean> {
     override fun process(input: CreateFileOptions): Boolean {
         input.codeGenerator.createNewFile(
             dependencies = Dependencies(true, *input.sourceFiles),
             packageName = input.outputPackage,
             fileName = input.fileName,
         ).use { stream ->
             stream.writer().use { input.fileSpec.writeTo(it) }
         }

         return true
    }
}