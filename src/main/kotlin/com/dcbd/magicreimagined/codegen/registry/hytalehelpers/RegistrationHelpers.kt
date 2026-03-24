package com.dcbd.hytale.ksp.registry.hytalehelpers

import com.dcbd.hytale.ksp.hytale_codec.api.CodecProvider
import com.dcbd.hytale.ksp.registry.annotations.RegisterComponent
import com.dcbd.hytale.ksp.registry.annotations.RegisterInteraction
import com.dcbd.hytale.ksp.registry.annotations.RegisterSystem
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.system.ISystem
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.findAnnotation

private object InteractionRegistrationHelperLogger

private val LOGGER = HytaleLogger.get(InteractionRegistrationHelperLogger::class.simpleName)


fun <T : Interaction> registerInteraction(
    kClass: KClass<T>,
    registry: CodecMapRegistry.Assets<Interaction, *>
) {
    val annotation = kClass.findAnnotation<RegisterInteraction>()
        ?: error("${kClass.simpleName} is missing @RegisterInteraction annotation")

    @Suppress("UNCHECKED_CAST")
    val companion = kClass.companionObjectInstance as? CodecProvider<T>
        ?: error("${kClass.simpleName} companion must implement CodecProvider")

    if (!annotation.enabled) {
        LOGGER.atWarning().log(
            "Registration for interaction %s is disabled. Skipping registration.",
            annotation.id
        )
        return
    }

    LOGGER.atInfo().log("Registering interaction: %s", annotation.id)

    registry.register(annotation.id, kClass.java, companion.CODEC)
}


fun <T : Component<EntityStore>> registerComponent(
    kClass: KClass<T>,
    registry: ComponentRegistryProxy<EntityStore>
) {
    val annotation = kClass.findAnnotation<RegisterComponent>()
        ?: error("${kClass.simpleName} is missing @RegisterComponent annotation")

    if (!annotation.enabled) {
        LOGGER.atWarning().log(
            "Registration for component %s is disabled. Skipping registration.",
            annotation.id
        )
        return
    }

    @Suppress("UNCHECKED_CAST")
    val codec = (kClass.companionObjectInstance as? CodecProvider<T>)?.CODEC
        ?: error("${kClass.simpleName} companion must implement CodecProvider")

    if((kClass.companionObjectInstance is ComponentTypeProvider<*>)) {
        LOGGER.atInfo().log("Registering component: %s", annotation.id)

        val registeredComponent: ComponentType<EntityStore, T> =
            registry.registerComponent(
                kClass.java,
                annotation.id,
                codec
            )

        @Suppress("UNCHECKED_CAST")
        (kClass.companionObjectInstance as ComponentTypeProvider<T>).componentType = registeredComponent
    } else {
        error("${kClass.simpleName} companion must implement ComponentTypeProvider")
    }
}

fun <T : ISystem<EntityStore>> registerSystem(
    kClass : KClass<T>,
    registry: ComponentRegistryProxy<EntityStore>
) {
    val annotation = kClass.findAnnotation<RegisterSystem>()
        ?: error("${kClass.simpleName} is missing @RegisterSystem annotation")

    if (!annotation.enabled) {
        LOGGER.atWarning().log(
            "Registration for component %s is disabled. Skipping registration.",
            kClass.simpleName
        )
        return
    }

    LOGGER.atInfo().log("Registering system: %s", kClass.simpleName)

    val system = kClass.objectInstance ?: run {
        val hasAutoRegistrableConstructor = kClass.constructors.any { constructor ->
            constructor.parameters.all(KParameter::isOptional)
        }

        check(hasAutoRegistrableConstructor) {
            "${kClass.simpleName} must be a Kotlin object or provide a public no-arg constructor to be auto-registered"
        }

        runCatching { kClass.createInstance() }
            .getOrElse { cause ->
                throw IllegalStateException(
                    "Failed to instantiate auto-registered system ${kClass.qualifiedName}",
                    cause
                )
            }
    }

    registry.registerSystem(system)

}