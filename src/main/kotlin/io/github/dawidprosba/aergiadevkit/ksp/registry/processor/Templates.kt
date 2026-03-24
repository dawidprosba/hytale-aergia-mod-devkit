package io.github.dawidprosba.aergiadevkit.ksp.registry.processor

fun interactionTemplate(
    outputPackage: String,
    interactionsMetadata: List<RegistryEntryMetadata>,
    pluginClass: String,
    outputObject: String = "InteractionRegistryGenerated"
): String {
    return """
package $outputPackage
import io.github.dawidprosba.aergiadevkit.ksp.registry.hytalehelpers.registerInteraction
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry

object $outputObject {
    val LOGGER = com.hypixel.hytale.logger.HytaleLogger.get($pluginClass::class.simpleName)
    fun registerAll(registry: CodecMapRegistry.Assets<Interaction, *>) {
${
        interactionsMetadata.joinToString("\n") { metadata ->
            if (!metadata.enabled) {
                "       LOGGER.atWarning().log(\"Skipping interaction '%s' (%s), reason -> disabled\", \"${metadata.qualifiedName}\")"
            } else {
                "       registerInteraction(${metadata.qualifiedName}::class, registry)"
            }
        }
    }
    }
}
""".trimIndent()
}

fun componentTemplate(
    outputPackage: String,
    interactionsMetadata: List<RegistryEntryMetadata>,
    pluginClass: String,
    outputObject: String = "ComponentRegistryGenerated"
): String {
    return """
package $outputPackage
import io.github.dawidprosba.aergiadevkit.ksp.registry.hytalehelpers.registerComponent
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

object $outputObject {
    val LOGGER = com.hypixel.hytale.logger.HytaleLogger.get($pluginClass::class.simpleName)
    fun registerAll(registry: ComponentRegistryProxy<EntityStore>) {
${
        interactionsMetadata.joinToString("\n") { metadata ->
            if (!metadata.enabled) {
                "       LOGGER.atWarning().log(\"Skipping component '%s' (%s), reason -> disabled\", \"${metadata.qualifiedName}\")"
            } else {
                "       registerComponent(${metadata.qualifiedName}::class, registry)"
            }
        }
    }
    }
}
""".trimIndent()
}

fun systemTemplate(
    outputPackage: String,
    interactionsMetadata: List<RegistryEntryMetadata>,
    pluginClass: String,
    outputObject: String = "SystemRegistryGenerated"
): String {
    return """
package $outputPackage
import io.github.dawidprosba.aergiadevkit.ksp.registry.hytalehelpers.registerSystem
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentRegistryProxy
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

object $outputObject {
    val LOGGER = com.hypixel.hytale.logger.HytaleLogger.get($pluginClass::class.simpleName)
    fun registerAll(registry: ComponentRegistryProxy<EntityStore>) {
${
        interactionsMetadata.joinToString("\n") { metadata ->
            if (!metadata.enabled) {
                "       LOGGER.atWarning().log(\"Skipping system '%s' (%s), reason -> disabled\", \"${metadata.qualifiedName}\")"
            } else {
                "       registerSystem(${metadata.qualifiedName}::class, registry)"
            }
        }
    }
    }
}
""".trimIndent()
}
