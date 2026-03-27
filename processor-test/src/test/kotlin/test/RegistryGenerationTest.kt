package test

import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegistryGenerationTest {

    private val generatedSourcesDir = File(System.getProperty("ksp.generated.sources"))

    private fun generatedContent(fileName: String): String {
        val file = generatedSourcesDir.resolve("test/generated/$fileName.kt")
        assertTrue(file.exists(), "$fileName.kt was not generated")
        return file.readText()
    }

    // --- ComponentRegistryGenerated ---

    @Test
    fun `generates ComponentRegistryGenerated file`() {
        assertTrue(
            generatedSourcesDir.resolve("test/generated/ComponentRegistryGenerated.kt").exists(),
            "ComponentRegistryGenerated.kt was not generated"
        )
    }

    @Test
    fun `component registry includes enabled component`() {
        assertContains(generatedContent("ComponentRegistryGenerated"), "registerComponent(TestComponent::class, registry)")
    }

    @Test
    fun `component registry skips disabled component with warning`() {
        assertContains(
            generatedContent("ComponentRegistryGenerated"),
            "\"test.fixtures.registry.DisabledComponent\""
        )
    }

    @Test
    fun `component registry does not register disabled component`() {
        assertFalse(
            generatedContent("ComponentRegistryGenerated").contains("registerComponent(DisabledComponent::class"),
            "Disabled component should not be registered"
        )
    }

    @Test
    fun `component registry logger is initialized with plugin class`() {
        assertContains(generatedContent("ComponentRegistryGenerated"), "HytaleLogger.get(TestPlugin::class.simpleName)")
    }

    // --- InteractionRegistryGenerated ---

    @Test
    fun `generates InteractionRegistryGenerated file`() {
        assertTrue(
            generatedSourcesDir.resolve("test/generated/InteractionRegistryGenerated.kt").exists(),
            "InteractionRegistryGenerated.kt was not generated"
        )
    }

    @Test
    fun `interaction registry includes enabled interaction`() {
        assertContains(generatedContent("InteractionRegistryGenerated"), "registerInteraction(TestInteraction::class, registry)")
    }

    @Test
    fun `interaction registry skips disabled interaction with warning`() {
        assertContains(
            generatedContent("InteractionRegistryGenerated"),
            "\"test.fixtures.registry.DisabledInteraction\""
        )
    }

    @Test
    fun `interaction registry does not register disabled interaction`() {
        assertFalse(
            generatedContent("InteractionRegistryGenerated").contains("registerInteraction(DisabledInteraction::class"),
            "Disabled interaction should not be registered"
        )
    }

    // --- SystemRegistryGenerated ---

    @Test
    fun `generates SystemRegistryGenerated file`() {
        assertTrue(
            generatedSourcesDir.resolve("test/generated/SystemRegistryGenerated.kt").exists(),
            "SystemRegistryGenerated.kt was not generated"
        )
    }

    @Test
    fun `system registry includes enabled system`() {
        assertContains(generatedContent("SystemRegistryGenerated"), "registerSystem(TestSystem::class, registry)")
    }

    @Test
    fun `system registry skips disabled system with warning`() {
        assertContains(
            generatedContent("SystemRegistryGenerated"),
            "\"test.fixtures.registry.DisabledSystem\""
        )
    }

    @Test
    fun `system registry does not register disabled system`() {
        assertFalse(
            generatedContent("SystemRegistryGenerated").contains("registerSystem(DisabledSystem::class"),
            "Disabled system should not be registered"
        )
    }

    // --- GlobalEventRegistryGenerated ---

    @Test
    fun `generates GlobalEventRegistryGenerated file`() {
        assertTrue(
            generatedSourcesDir.resolve("test/generated/GlobalEventRegistryGenerated.kt").exists(),
            "GlobalEventRegistryGenerated.kt was not generated"
        )
    }

    @Test
    fun `global event registry suppresses unchecked cast`() {
        assertContains(generatedContent("GlobalEventRegistryGenerated"), """@file:Suppress("UNCHECKED_CAST")""")
    }

    @Test
    fun `global event without param uses direct call form`() {
        assertContains(
            generatedContent("GlobalEventRegistryGenerated"),
            """registerGlobalEvent(StubEvent::class, { TestEventHandlers.Companion.onGlobalEvent() }, registry, "onGlobalEvent")"""
        )
    }

    @Test
    fun `global event with param casts event to concrete type`() {
        assertContains(
            generatedContent("GlobalEventRegistryGenerated"),
            """registerGlobalEvent(StubEvent::class, { event -> TestEventHandlers.Companion.onGlobalEventWithParam(event as test.fixtures.registry.StubEvent) }, registry, "onGlobalEventWithParam")"""
        )
    }

    @Test
    fun `global event with type param casts event to upper bound type`() {
        assertContains(
            generatedContent("GlobalEventRegistryGenerated"),
            """registerGlobalEvent(StubEvent::class, { event -> TestEventHandlers.Companion.onGlobalEventWithTypeParam(event as test.fixtures.registry.StubEvent) }, registry, "onGlobalEventWithTypeParam")"""
        )
    }

    @Test
    fun `global event registry skips disabled handler with warning`() {
        assertContains(
            generatedContent("GlobalEventRegistryGenerated"),
            "\"test.fixtures.registry.TestEventHandlers.Companion.onDisabledGlobalEvent\""
        )
    }

    @Test
    fun `global event registry does not register disabled handler`() {
        assertFalse(
            generatedContent("GlobalEventRegistryGenerated").contains("registerGlobalEvent(StubEvent::class, { TestEventHandlers.Companion.onDisabledGlobalEvent"),
            "Disabled global event handler should not be registered"
        )
    }

    // --- EventRegistryGenerated ---

    @Test
    fun `generates EventRegistryGenerated file`() {
        assertTrue(
            generatedSourcesDir.resolve("test/generated/EventRegistryGenerated.kt").exists(),
            "EventRegistryGenerated.kt was not generated"
        )
    }

    @Test
    fun `event registry suppresses unchecked cast`() {
        assertContains(generatedContent("EventRegistryGenerated"), """@file:Suppress("UNCHECKED_CAST")""")
    }

    @Test
    fun `event without param includes subject class and uses direct call form`() {
        assertContains(
            generatedContent("EventRegistryGenerated"),
            """registerEvent(StubEvent::class, StubSubject::class.java, { TestEventHandlers.Companion.onEvent() }, registry, "onEvent")"""
        )
    }

    @Test
    fun `event with param includes subject class and casts event to concrete type`() {
        assertContains(
            generatedContent("EventRegistryGenerated"),
            """registerEvent(StubEvent::class, StubSubject::class.java, { event -> TestEventHandlers.Companion.onEventWithParam(event as test.fixtures.registry.StubEvent) }, registry, "onEventWithParam")"""
        )
    }

    @Test
    fun `event with type param casts event to upper bound type`() {
        assertContains(
            generatedContent("EventRegistryGenerated"),
            """registerEvent(StubEvent::class, StubSubject::class.java, { event -> TestEventHandlers.Companion.onEventWithTypeParam(event as test.fixtures.registry.StubEvent) }, registry, "onEventWithTypeParam")"""
        )
    }

    @Test
    fun `event registry skips disabled handler with warning`() {
        assertContains(
            generatedContent("EventRegistryGenerated"),
            "\"test.fixtures.registry.TestEventHandlers.Companion.onDisabledEvent\""
        )
    }

    @Test
    fun `event registry does not register disabled handler`() {
        assertFalse(
            generatedContent("EventRegistryGenerated").contains("registerEvent(StubEvent::class, StubSubject::class.java, { TestEventHandlers.Companion.onDisabledEvent"),
            "Disabled event handler should not be registered"
        )
    }
}
