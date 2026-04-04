package test.fixtures.registry

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.HytaleComponent
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterComponent

@HytaleComponent("test_component")
class TestComponent : Component<EntityStore> {
    @CodecProperty("The test value")
    var testValue: String = ""
    override fun clone(): Component<EntityStore> = TestComponent()
}

@HytaleComponent("disabled_component", enabled = false)
class DisabledComponent : Component<EntityStore> {
    override fun clone(): Component<EntityStore> = DisabledComponent()
}

@Suppress("DEPRECATION")
@RegisterComponent("legacy_component")
class LegacyComponent : Component<EntityStore> {
    override fun clone(): Component<EntityStore> = LegacyComponent()
}
