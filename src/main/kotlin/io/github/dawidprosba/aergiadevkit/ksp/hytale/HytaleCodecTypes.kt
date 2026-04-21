package io.github.dawidprosba.aergiadevkit.ksp.hytale

import com.squareup.kotlinpoet.ClassName

internal val HYTALE_CODEC_TYPE = ClassName("com.hypixel.hytale.codec", "Codec")
internal val HYTALE_KEYED_CODEC_TYPE = ClassName("com.hypixel.hytale.codec", "KeyedCodec")
internal val HYTALE_BUILDER_CODEC_TYPE =
    ClassName("com.hypixel.hytale.codec.builder", "BuilderCodec")
