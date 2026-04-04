package io.github.dawidprosba.aergiadevkit.ksp.registry.providers

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

interface ComponentRegistrationService {
    fun registerAll(registry: ComponentRegistryProxy<EntityStore>): MutableMap<Class<out Component<EntityStore>>, ComponentType<EntityStore, *>>
}
