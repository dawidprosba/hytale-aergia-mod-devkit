---
sidebar_position: 2
---

import Version from '@site/src/components/Version';

# Getting Started

## Installation

:::warning
This library is for Kotlin projects only. It uses KSP (Kotlin Symbol Processing) to generate code at compile time — pure Java projects are not supported.
:::

Apply the KSP plugin and add the devkit as a dependency:

```kotlin
// build.gradle.kts
plugins {
    // KSP version must match your Kotlin version: <kotlin-version>-<ksp-version>
    // See https://github.com/google/ksp/releases for the full list
    id("com.google.devtools.ksp") version "2.1.20-1.0.32"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.dawidprosba:hytale-aergia-mod-devkit:<Version />")
    ksp("io.github.dawidprosba:hytale-aergia-mod-devkit:<Version />")
}

ksp {
    // Package where generated registries will be placed
    arg("registriesOutputPackage", "com.example.mymod.registries.generated")
    // Your mod's main plugin class (used in generated registry bootstrapper)
    arg("pluginClass", "com.example.mymod.MyMod")
}
```

## Wire up the generated registries

Aergia generates `ComponentRegistryGenerated`, `SystemRegistryGenerated`, and `InteractionRegistryGenerated` into the package you configured in `registriesOutputPackage`. Call them once during your mod's `setup()`:

```kotlin
class MyMod(init: JavaPluginInit) : JavaPlugin(init) {

    override fun setup() {
        register()
    }

    private fun register() {
        ComponentRegistryGenerated.registerAll(
            registry = this.entityStoreRegistry
        )
        SystemRegistryGenerated.registerAll(
            registry = this.entityStoreRegistry
        )
        InteractionRegistryGenerated.registerAll(
            registry = this.getCodecRegistry(Interaction.CODEC)
        )
    }
}
```

That's it — every class annotated with `@RegisterComponent`, `@RegisterSystem`, or `@RegisterInteraction` is now registered.
