package test.fixtures.registry

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterInteraction

@RegisterInteraction("test_interaction")
abstract class TestInteraction : Interaction()

@RegisterInteraction("disabled_interaction", enabled = false)
abstract class DisabledInteraction : Interaction()
