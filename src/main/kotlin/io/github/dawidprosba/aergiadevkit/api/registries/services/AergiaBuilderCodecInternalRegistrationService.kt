package io.github.dawidprosba.aergiadevkit.api.registries.services

import com.hypixel.hytale.codec.builder.BuilderCodec

interface AergiaBuilderCodecInternalRegistrationService {
    fun registerAll(): MutableMap<Class<*>, BuilderCodec<*>>
}