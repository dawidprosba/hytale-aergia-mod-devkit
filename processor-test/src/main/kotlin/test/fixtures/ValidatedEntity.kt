package test.fixtures

import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.GenerateCodec
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.validators.CodecRequiredValidator

@GenerateCodec
class ValidatedEntity {
    @CodecProperty("A field that must not be null")
    @CodecRequiredValidator
    var requiredField: String = ""
}
