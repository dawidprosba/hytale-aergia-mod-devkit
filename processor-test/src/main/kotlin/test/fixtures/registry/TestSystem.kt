package test.fixtures.registry

import com.hypixel.hytale.component.system.ISystem
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterSystem

@RegisterSystem
class TestSystem : ISystem<EntityStore>

@RegisterSystem(enabled = false)
class DisabledSystem : ISystem<EntityStore>
