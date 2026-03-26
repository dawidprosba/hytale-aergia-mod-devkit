---
title: Getting Started
sidebar_position: 2
---

import Version from '@site/src/components/Version';
import CodeWithSteps from '@site/src/components/CodeWithSteps';

# Getting Started

## Installation

:::warning
This library is for Kotlin projects only. It uses KSP (Kotlin Symbol Processing) to generate code at compile time — pure Java projects are not supported.
:::

Apply the KSP plugin and add the devkit as a dependency:

<CodeWithSteps language="kotlin" steps={[
  "Apply the KSP plugin for compile-time code generation.",
  "Add the devkit as both a runtime dependency and a KSP processor, You either specify version or use '0.+' to always get the latest of the alpha versions.",
  "Configure the output package for generated registries and your plugin's main class.",
]}>
{`// build.gradle.kts
plugins {
    kotlin("jvm") version "2.3.20"
    id("hytale-mod") version "0.+"
    // add-next-line
    id("com.google.devtools.ksp") version "2.3.6" // (1)
}

repositories {
    mavenCentral()
}

dependencies {
    // add-start
    implementation("io.github.dawidprosba:hytale-aergia-mod-devkit:0.+") // (2)
    ksp("io.github.dawidprosba:hytale-aergia-mod-devkit:0.+")
    // add-end
}

ksp {
    
    arg("registriesOutputPackage", "com.example.mymod.registries.generated") // (3)
    arg("pluginClass", "com.example.mymod.MyMod")
}`}
</CodeWithSteps>

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
