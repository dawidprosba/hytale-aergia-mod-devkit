package example_mod.components

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.api.registries.annotations.HytaleComponent

@HytaleComponent(id = "example_component", enabled = false)
class ExampleComponent : Component<EntityStore> {
    override fun clone(): Component<EntityStore?>? {
        TODO("Not yet implemented")
    }
}