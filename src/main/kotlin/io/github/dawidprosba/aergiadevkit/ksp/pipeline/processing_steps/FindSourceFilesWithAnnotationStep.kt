package io.github.dawidprosba.aergiadevkit.ksp.pipeline.processing_steps

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import io.github.dawidprosba.aergiadevkit.ksp.pipeline.PipelineStep

class FindSourceFilesWithAnnotationStep(val addDeferredSymbols: (deferred: List<KSAnnotated>) -> Unit) : PipelineStep<List<KSAnnotated>, Set<KSFile>> {
    override fun process(input: List<KSAnnotated>): Set<KSFile> {
        addDeferredSymbols(
            input.filterNot { it.validate() }
        )

        return input.filter { it.validate() }
            .mapNotNull { it.containingFile }
            .toHashSet()
    }
}