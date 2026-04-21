package io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.data

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

data class CodecGeneratorEntryMetadata(val qualifiedName: String, val enabled: Boolean, val ksClassDeclaration: KSClassDeclaration, val propertiesMarkedWithCodec : List<KSPropertyDeclaration>)