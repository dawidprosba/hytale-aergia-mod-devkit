package io.github.dawidprosba.aergiadevkit.api.registries


internal interface AergiaRegistry {
    /**
     * Registers all entries of this registry.
     */
    fun registerAll(): Int
}