package test.api.registries

import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.api.registries.AergiaComponentRegistry
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import test.MockedComponentServiceLoader
import kotlin.test.BeforeTest
import kotlin.test.assertEquals


class AergiaComponentRegistryRegisterAllTest {
    lateinit var hytaleComponentRegistry: ComponentRegistryProxy<EntityStore>
    lateinit var aergiaComponentRegistry: AergiaComponentRegistry

    @BeforeTest
    fun setUp() {
        hytaleComponentRegistry = mockk<ComponentRegistryProxy<EntityStore>>(relaxed = true)
        aergiaComponentRegistry = AergiaComponentRegistry(hytaleComponentRegistry)
    }

    @Test
    fun `No services found, no components registered`() {
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