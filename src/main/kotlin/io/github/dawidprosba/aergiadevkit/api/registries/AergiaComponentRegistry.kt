package io.github.dawidprosba.aergiadevkit.api.registries

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.ksp.registry.providers.ComponentRegistrationService
import java.util.ServiceLoader

/**
 * Registry that loads all your mod components in one place and allows you to register
 * them with just one function call (registerAll(YourPluginClass)).
 *
 * The Registry knows which component to automatically register based on class annotation `@HytaleComponent`.
 */
class AergiaComponentRegistry {
    companion object {
        /**
         * After registering component with Hytale registry, we get ComponentType of that component
         * that can be later used for getting that component from a holder.
         * This map holds those types after registering a component.
         */
        @PublishedApi
        internal val registeredComponentsType =
            mutableMapOf<Class<out Component<EntityStore>>, ComponentType<EntityStore, *>>()

        /**
         * KSP Generated Services.
         */
        private val componentRegistrationService: List<ComponentRegistrationService> by lazy {
            ServiceLoader.load(ComponentRegistrationService::class.java).toList()
        }

        /**
         * Gets the [ComponentType] for your mod component.
         * Component class must be annotated with `@HytaleComponent`.
         * Must be called after [registerAll].
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Component<EntityStore>> getComponentType(): ComponentType<EntityStore, T> {
            return registeredComponentsType[T::class.java] as? ComponentType<EntityStore, T>
                ?: throw IllegalArgumentException(
                    "No component type found for class: ${T::class.java.name}, " +
                            "make sure your component is annotated with @HytaleComponent and registered properly using `AergiaComponentRegistry.registerAll`"
                )
        }

        /**
         * Returns a lazy delegate that resolves the [ComponentType] for your mod component on first access.
         * Component class must be annotated with `@HytaleComponent`.
         *
         * Usage: `val componentType by AergiaComponentRegistry.lazyGetComponentType<MyComponent>()`
         */
        inline fun <reified T : Component<EntityStore>> lazyGetComponentType(): Lazy<ComponentType<EntityStore, T>> =
            lazy { getComponentType<T>() }

        /**
         * Registers all components with @HytaleComponent annotation.
         * @param plugin - Your Hytale Mod Main Class
         */
        fun registerAll(plugin: JavaPlugin) {
            val logger = plugin.logger
            if (componentRegistrationService.isEmpty()) {
                logger.atWarning().log("No component registration services found.")
            }
            componentRegistrationService.forEach {
                it.registerAll(plugin.entityStoreRegistry).forEach { (clazz, type) ->
                    registeredComponentsType[clazz] = type
                }
            }
            logger.atInfo().log("Registered ${registeredComponentsType.size} components.")
        }
    }
}