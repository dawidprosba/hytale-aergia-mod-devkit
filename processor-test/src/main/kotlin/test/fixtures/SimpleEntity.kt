package test.fixtures

import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.GenerateCodec

@GenerateCodec
class SimpleEntity {
    @CodecProperty("The name of the entity")
    var name: String = ""

    @CodecProperty("The health of the entity")
    var health: Float = 0f

    @CodecProperty("Whether the entity is active")
    var active: Boolean = false
}
