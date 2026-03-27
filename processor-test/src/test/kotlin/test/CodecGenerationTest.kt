package test

import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CodecGenerationTest {

    private val generatedSourcesDir = File(System.getProperty("ksp.generated.sources"))

    private fun generatedContent(fileName: String): String {
        val file = generatedSourcesDir.resolve("test/fixtures/$fileName.kt")
        assertTrue(file.exists(), "$fileName.kt was not generated")
        return file.readText()
    }

    // --- SimpleEntity ---

    @Test
    fun `generates codec file for SimpleEntity`() {
        assertTrue(
            generatedSourcesDir.resolve("test/fixtures/SimpleEntityCodec.kt").exists(),
            "SimpleEntityCodec.kt was not generated"
        )
    }

    @Test
    fun `string property resolves to Codec STRING`() {
        assertContains(generatedContent("SimpleEntityCodec"), "Codec.STRING")
    }

    @Test
    fun `float property resolves to Codec FLOAT`() {
        assertContains(generatedContent("SimpleEntityCodec"), "Codec.FLOAT")
    }

    @Test
    fun `boolean property resolves to Codec BOOLEAN`() {
        assertContains(generatedContent("SimpleEntityCodec"), "Codec.BOOLEAN")
    }

    @Test
    fun `non-inherited property uses append`() {
        val content = generatedContent("SimpleEntityCodec")
        assertContains(content, ".append(")
        assertFalse(content.contains(".appendInherited("), "Non-inherited property should not use appendInherited")
    }

    @Test
    fun `documentation is present in generated codec`() {
        assertContains(generatedContent("SimpleEntityCodec"), "The name of the entity")
    }

    // --- ChildEntity (inheritance) ---

    @Test
    fun `generates codec file for ChildEntity`() {
        assertTrue(
            generatedSourcesDir.resolve("test/fixtures/ChildEntityCodec.kt").exists(),
            "ChildEntityCodec.kt was not generated"
        )
    }

    @Test
    fun `inherited property uses appendInherited`() {
        assertContains(generatedContent("ChildEntityCodec"), ".appendInherited(")
    }

    @Test
    fun `own property of child uses append`() {
        assertContains(generatedContent("ChildEntityCodec"), ".append(")
    }

    // --- ValidatedEntity ---

    @Test
    fun `generates codec file for ValidatedEntity`() {
        assertTrue(
            generatedSourcesDir.resolve("test/fixtures/ValidatedEntityCodec.kt").exists(),
            "ValidatedEntityCodec.kt was not generated"
        )
    }

    @Test
    fun `CodecRequiredValidator adds addValidator call`() {
        assertContains(generatedContent("ValidatedEntityCodec"), ".addValidator(")
    }

    @Test
    fun `CodecRequiredValidator uses nonNull validator`() {
        assertContains(generatedContent("ValidatedEntityCodec"), "Validators.nonNull()")
    }
}
