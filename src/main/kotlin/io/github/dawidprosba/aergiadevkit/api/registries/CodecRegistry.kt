package io.github.dawidprosba.aergiadevkit.api.registries

import com.hypixel.hytale.codec.builder.BuilderCodec
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.InjectCodec
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.providers.CodecRegistrationService
import java.util.ServiceLoader
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

object CodecRegistry {
    private val codecs: Map<Class<*>, BuilderCodec<*>> by lazy {
        val result = mutableMapOf<Class<*>, BuilderCodec<*>>()
        ServiceLoader.load(CodecRegistrationService::class.java).forEach { service ->
            service.registerAll().forEach { (clazz, codec) ->
                result[clazz] = codec
                populateLateInitCodec(clazz, codec)
            }
        }
        result
    }

    private fun populateLateInitCodec(clazz: Class<*>, codec: BuilderCodec<*>) {
        val companionInstance = clazz.kotlin.companionObjectInstance ?: return

        companionInstance::class.memberProperties
            .filter { it.findAnnotation<InjectCodec>() != null }
            .forEach { property ->
                val backingField = property.javaField ?: return@forEach
                backingField.isAccessible = true
                backingField.set(companionInstance, codec)
            }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getCodec(clazz: Class<T>): BuilderCodec<T>? = codecs[clazz] as? BuilderCodec<T>
}
