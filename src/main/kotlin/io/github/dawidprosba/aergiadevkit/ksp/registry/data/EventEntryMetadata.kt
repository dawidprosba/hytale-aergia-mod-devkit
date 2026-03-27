package io.github.dawidprosba.aergiadevkit.ksp.registry.data

data class EventEntryMetadata(
    val functionQualifiedName: String,
    val containingClassQualifiedName: String,
    val functionName: String,
    val eventClassQualifiedName: String,
    val eventParamTypeName: String?,
    val subjectClassQualifiedName: String,
    val hasEventParam: Boolean,
    val enabled: Boolean,
)
