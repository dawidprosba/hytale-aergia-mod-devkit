package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.data

data class CodecPropertyData(
    val documentation: String,
    val required: Boolean,
    val treatAsInherited: Boolean,
    val defaultValue: String?,
) {
    constructor(
        annotationArgs: Map<String, Any?>,
        isInherited: Boolean = false,
    ) : this(
        documentation = annotationArgs["documentation"] as? String? ?: error("Missing documentation."),
        required = annotationArgs["required"] as? Boolean? ?: false,
        defaultValue = annotationArgs["defaultValue"] as? String?,
        treatAsInherited = isInherited,
    )

    init {
        require(documentation.isNotBlank()) { "Documentation cannot be blank." }
        require(defaultValue != "") { "Default value cannot be empty string. Pass null instead." }
    }
}
