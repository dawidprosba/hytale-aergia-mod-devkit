package io.github.dawidprosba.aergiadevkit.ksp.registry.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

internal val HYTALE_LOGGER_TYPE = ClassName("com.hypixel.hytale.logger", "HytaleLogger")
internal val CODEC_MAP_REGISTRY_ASSETS_TYPE =
    ClassName("com.hypixel.hytale.server.core.plugin.registry", "CodecMapRegistry", "Assets")
internal val INTERACTION_TYPE = ClassName(
    "com.hypixel.hytale.server.core.modules.interaction.interaction.config",
    "Interaction"
)
internal val COMPONENT_REGISTRY_PROXY_TYPE =
    ClassName("com.hypixel.hytale.component", "ComponentRegistryProxy")
internal val ENTITY_STORE_TYPE =
    ClassName("com.hypixel.hytale.server.core.universe.world.storage", "EntityStore")

internal val REGISTER_INTERACTION = MemberName(
    "io.github.dawidprosba.aergiadevkit.ksp.registry.hytalehelpers",
    "registerInteraction"
)
internal val REGISTER_COMPONENT = MemberName(
    "io.github.dawidprosba.aergiadevkit.ksp.registry.hytalehelpers",
    "registerComponent"
)
internal val REGISTER_SYSTEM = MemberName(
    "io.github.dawidprosba.aergiadevkit.ksp.registry.hytalehelpers",
    "registerSystem"
)
