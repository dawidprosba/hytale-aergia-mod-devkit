package test.api.registries

import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.api.registries.AergiaComponentRegistry
import io.github.dawidprosba.aergiadevkit.api.registries.services.AergiaComponentRegistrationService
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.MockedComponentServiceLoader


class AergiaComponentRegistryRegisterAllTest {
    lateinit var hytaleComponentRegistry: ComponentRegistryProxy<EntityStore>
    lateinit var aergiaComponentRegistry: AergiaComponentRegistry

    @BeforeEach
    fun setUp() {
        hytaleComponentRegistry = mockk<ComponentRegistryProxy<EntityStore>>(relaxed = true)
        aergiaComponentRegistry = AergiaComponentRegistry(hytaleComponentRegistry)
    }

    @Test
    fun `No services found, no components registered`() {
        val field =
        AergiaComponentRegistry::class.java.getDeclaredField("componentRegistrationService\$delegate")
        field.isAccessible = true
        field.set(aergiaComponentRegistry, lazy { emptyList<AergiaComponentRegistrationService>() })

        val registeredComponentsCount = aergiaComponentRegistry.registerAll()

        assertEquals(0, registeredComponentsCount)
    }

    @Test
    fun `Registers components when service returns components`() {
        val field =
        AergiaComponentRegistry::class.java.getDeclaredField("componentRegistrationService\$delegate")
        field.isAccessible = true
        field.set(aergiaComponentRegistry, lazy { listOf(MockedComponentServiceLoader()) })

        val registeredComponentsCount = aergiaComponentRegistry.registerAll()

        assertEquals(1, registeredComponentsCount)
    }
}