package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.providers

import com.hypixel.hytale.codec.builder.BuilderCodec

interface CodecRegistrationService {
    fun registerAll(): MutableMap<Class<*>, BuilderCodec<*>>
}
