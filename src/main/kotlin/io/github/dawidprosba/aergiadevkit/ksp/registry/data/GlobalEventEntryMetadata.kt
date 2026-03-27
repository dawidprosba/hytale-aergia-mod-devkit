package io.github.dawidprosba.aergiadevkit.ksp.registry.data

data class GlobalEventEntryMetadata(
    val functionQualifiedName: String,
    val containingClassQualifiedName: String,
    val functionName: String,
    val eventClassQualifiedName: String,
    val eventParamTypeName: String?,
    val hasEventParam: Boolean,
    val enabled: Boolean,
)
