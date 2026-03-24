package com.dcbd.aergiadevkit.ksp.hytale_codec.api

import com.hypixel.hytale.codec.builder.BuilderCodec

interface CodecProvider<T> {
    val CODEC: BuilderCodec<T>
}