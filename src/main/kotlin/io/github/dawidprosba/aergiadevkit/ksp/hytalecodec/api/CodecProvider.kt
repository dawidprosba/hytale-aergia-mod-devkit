package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.api

import com.hypixel.hytale.codec.builder.BuilderCodec

interface CodecProvider<T> {
    val CODEC: BuilderCodec<T>
}