package test.fixtures.registry

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterComponent

@RegisterComponent("test_component")
class TestComponent : Component<EntityStore> {
    override fun clone(): Component<EntityStore> = TestComponent()
}

@RegisterComponent("disabled_component", enabled = false)
class DisabledComponent : Component<EntityStore> {
    override fun clone(): Component<EntityStore> = DisabledComponent()
}
