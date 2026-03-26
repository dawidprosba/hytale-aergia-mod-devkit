package io.github.dawidprosba.aergiadevkit.ksp.registry.processor

open class RegistryEntryMetadata(val qualifiedName: String, val enabled: Boolean)

class GlobalEventEntryMetadata(
    val functionQualifiedName: String,
    val containingClassQualifiedName: String,
    val functionName: String,
    val eventClassQualifiedName: String,
    val eventParamTypeName: String?,
    val hasEventParam: Boolean,
    val enabled: Boolean
)

class EventEntryMetadata(
    val functionQualifiedName: String,
    val containingClassQualifiedName: String,
    val functionName: String,
    val eventClassQualifiedName: String,
    val eventParamTypeName: String?,
    val subjectClassQualifiedName: String,
    val hasEventParam: Boolean,
    val enabled: Boolean
)
