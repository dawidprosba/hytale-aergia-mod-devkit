package io.github.dawidprosba.aergiadevkit.api.registries.services

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

/**
 * Registration service (should be generated automatically by ksp processor).
 */
interface AergiaComponentRegistrationService {
    fun registerAll(registry: ComponentRegistryProxy<EntityStore>): Map<Class<out Component<EntityStore>>, ComponentType<EntityStore, *>>
}
