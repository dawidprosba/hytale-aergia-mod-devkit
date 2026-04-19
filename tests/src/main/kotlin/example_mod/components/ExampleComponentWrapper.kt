package example_mod.components

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

open class ExampleComponentWrapper :  Component<EntityStore> {
    override fun clone(): Component<EntityStore?>? {
        TODO("Not yet implemented")
    }
}
