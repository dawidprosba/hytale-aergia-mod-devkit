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
        private val registeredComponentsType =
            mutableMapOf<Class<out Component<EntityStore>>, ComponentType<EntityStore, *>>()

        /**
         * KSP Generated Services.
         */
        private val componentRegistrationService: List<ComponentRegistrationService> by lazy {
            ServiceLoader.load(ComponentRegistrationService::class.java).toList()
        }
        
        /**
         * Gets component type for your mod component.
         * Component class must be annotated with `@HytaleComponent`
         */
        fun <T : Component<EntityStore>> getComponentType(clazz: Class<T>): ComponentType<EntityStore, T> {
            val foundComponentType =
                registeredComponentsType[clazz] ?: throw IllegalArgumentException(
                    "No component type found for class: ${clazz.name}," + " make sure your component is annotated with @HytaleComponent and registered properly using `AergiaComponentRegistry.registerAll`"
                )
            @Suppress("UNCHECKED_CAST") return foundComponentType as ComponentType<EntityStore, T>
        }

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