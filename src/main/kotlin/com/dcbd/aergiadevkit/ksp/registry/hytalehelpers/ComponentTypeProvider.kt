package com.dcbd.aergiadevkit.ksp.registry.hytalehelpers

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

interface ComponentTypeProvider<T : Component<EntityStore>> {
    var componentType: ComponentType<EntityStore, T>?
}