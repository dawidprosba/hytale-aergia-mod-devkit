package test.fixtures

import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.GenerateCodec

open class BaseEntity {
    @CodecProperty("The base display name")
    open var baseName: String = ""
}

@GenerateCodec
class ChildEntity : BaseEntity() {
    @CodecProperty("The child-specific value")
    var childField: String = ""
}
