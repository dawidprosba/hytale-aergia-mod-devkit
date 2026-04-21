package io.github.dawidprosba.aergiadevkit.api.registries

import com.hypixel.hytale.codec.builder.BuilderCodec
import io.github.dawidprosba.aergiadevkit.api.registries.services.AergiaBuilderCodecInternalRegistrationService
import java.util.ServiceLoader

/**
 * Automated registry that contains list of generated BuilderCodecs for annotated classes.
 */
object AergiaInternalBuilderCodecRegistry {
    private val codecs: Map<Class<*>, BuilderCodec<*>> by lazy {
        val result = mutableMapOf<Class<*>, BuilderCodec<*>>()
        ServiceLoader.load(AergiaBuilderCodecInternalRegistrationService::class.java).forEach { service ->
            service.registerAll().forEach { (classHoldingCodec, codec) ->
                result[classHoldingCodec] = codec
            }
        }
        result
    }

    /**
     * Gets the BuilderCodec for the given mod class. Throws an exception if no codec is found.
     * @param clazz the class that has the annotations for generating codecs.
     * @throws IllegalArgumentException if no codec is found for the given class.
     * @return CodecBuilder generated based on annotations.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getCodec(clazz: Class<T>): BuilderCodec<T> {
        val foundCodec = codecs[clazz]
            ?: throw IllegalArgumentException("No codec found for class: ${clazz.name}, consider using @GenerateCodec.")

        return foundCodec as BuilderCodec<T>
    }
}