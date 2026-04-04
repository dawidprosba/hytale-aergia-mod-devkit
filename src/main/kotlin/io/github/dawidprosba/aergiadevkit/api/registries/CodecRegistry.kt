package io.github.dawidprosba.aergiadevkit.api.registries

import com.hypixel.hytale.codec.builder.BuilderCodec
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.providers.CodecRegistrationService
import java.util.ServiceLoader

object CodecRegistry {
    private val codecs: Map<Class<*>, BuilderCodec<*>> by lazy {
        val result = mutableMapOf<Class<*>, BuilderCodec<*>>()
        ServiceLoader.load(CodecRegistrationService::class.java).forEach { service ->
            result.putAll(service.registerAll())
        }
        result
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getCodec(clazz: Class<T>): BuilderCodec<T>? = codecs[clazz] as? BuilderCodec<T>
}
