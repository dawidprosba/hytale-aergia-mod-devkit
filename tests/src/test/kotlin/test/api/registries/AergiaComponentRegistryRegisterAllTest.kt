package test.api.registries

import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.api.registries.AergiaComponentRegistry
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals


class AergiaComponentRegistryRegisterAllTest {

    @Test
    fun `No services found, not components registered`() {
        val componentRegistry = mockk<ComponentRegistryProxy<EntityStore>>(relaxed = true)

        val aergiaComponentRegistry = AergiaComponentRegistry(componentRegistry)
        val registeredComponentsCount = aergiaComponentRegistry.registerAll()

        assertEquals(0, registeredComponentsCount)
    }
}