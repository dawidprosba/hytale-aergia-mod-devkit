package test

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.api.registries.services.AergiaComponentRegistrationService

class MockedComponentServiceLoader() : AergiaComponentRegistrationService {
    @Suppress("UNCHECKED_CAST")
    override fun registerAll(registry: ComponentRegistryProxy<EntityStore>): MutableMap<Class<out Component<EntityStore>>, ComponentType<EntityStore, *>> {
        val result = mutableMapOf<Class<out Component<EntityStore>>, ComponentType<EntityStore, *>>()
        (result as MutableMap<Class<out Component<EntityStore>>, Any?>)[Component::class.java as Class<out Component<EntityStore>>] = null
        return result
    }
}