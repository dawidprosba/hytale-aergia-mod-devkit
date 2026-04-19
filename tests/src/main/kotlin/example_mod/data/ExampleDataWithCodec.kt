package example_mod.data

import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.GenerateCodec

@GenerateCodec
data class ExampleDataWithCodec(
    @param:CodecProperty("Test Data Param") var test: String = "test value"
)
