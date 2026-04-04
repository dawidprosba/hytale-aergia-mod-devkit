package io.github.dawidprosba.aergiadevkit.api.registries

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.ksp.registry.providers.ComponentRegistrationService
import java.util.ServiceLoader

class ComponentRegistry {
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


        @Suppress("UNCHECKED_CAST")
        fun <T : Component<EntityStore>> getComponentType(clazz: Class<T>): ComponentType<EntityStore, T>? {
            return registeredComponentsType[clazz] as? ComponentType<EntityStore, T>
        }

        /**
         * Registers all components with @HytaleComponent annotation.
         * @param plugin - Your Hytale Mod Main Class
         */
        public fun registerAll(plugin: JavaPlugin) {
            val logger = plugin.logger
            if(componentRegistrationService.isEmpty()) {
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