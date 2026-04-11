package io.github.dawidprosba.aergiadevkit.api.registries

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.api.registries.services.AergiaComponentRegistrationService
import java.util.ServiceLoader


class AergiaComponentRegistry(val componentRegistry: ComponentRegistryProxy<EntityStore>) : AergiaRegistry {
    private val registeredComponentsType =
        mutableMapOf<Class<out Component<EntityStore>>, ComponentType<EntityStore, *>>()

    /**
     * KSP Generated Services.
     */
    private val componentRegistrationService: List<AergiaComponentRegistrationService> by lazy {
        ServiceLoader.load(AergiaComponentRegistrationService::class.java).toList()
    }

    /**
     * Registers component annotated with `@HytaleComponent` annotation.
     * Note: This method should be called inside `setup()` of your plugin class.
     */
    override fun registerAll(): Int {
        if (componentRegistrationService.isEmpty()) {
            return 0
        }

        componentRegistrationService.forEach {
            it.registerAll(componentRegistry).forEach { (clazz, type) ->
                registeredComponentsType[clazz] = type
            }
        }
        return registeredComponentsType.size
    }

    /**
     * Gets the [ComponentType] for your mod component.
     * Component class must be annotated with `@HytaleComponent`.
     * Must be called after [registerAll].
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T : Component<EntityStore>> getComponentType(componentClass: Class<T>): ComponentType<EntityStore, T> {
        return registeredComponentsType[componentClass] as? ComponentType<EntityStore, T>
            ?: throw IllegalArgumentException(
                "No component type found for class: ${componentClass}, " +
                        "make sure your component is annotated with @HytaleComponent and registered properly using `AergiaComponentRegistry.registerAll`"
            )
    }
}
