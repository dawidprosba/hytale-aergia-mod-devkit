<div align="center">

# Hytale Aergia Mod Devkit

*Aergia, goddess of laziness.*

A KSP annotation processor library for Hytale mod development.
Annotate your classes — Aergia generates the boilerplate at compile time.
You focus on the creative work.

![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)
![KSP](https://img.shields.io/badge/KSP-2.x-orange)
![License](https://img.shields.io/badge/license-MIT-blue)
![Status](https://img.shields.io/badge/status-work%20in%20progress-yellow)

> **This library is a work in progress.** APIs may change, and new annotations are being added alongside active mod development.

> **[Full Documentation](https://dawidprosba.github.io/hytale-aergia-mod-devkit/latest/)**

</div>

---

## What gets generated?

### Codec annotations

| Annotation | Target | Description |
|---|---|---|
| `@GenerateCodec` | `class` | Generates a `BuilderCodec` for the annotated class |
| `@CodecProperty` | `property`, `value parameter` | Marks a field for inclusion in the generated codec |
| `@CodecRequiredValidator` | `property`, `value parameter` | Adds a non-null validator to a codec field |
| `@CodecProjectileValidator` | `property`, `value parameter` | Adds the Hytale projectile asset validator to a codec field |

### Registry annotations

| Annotation | Target | Description |
|---|---|---|
| `@RegisterComponent` | `class` | Registers a component in the Hytale component registry |
| `@RegisterSystem` | `class` | Registers a system in the entity store registry |
| `@RegisterInteraction` | `class` | Registers an interaction in the codec map registry |
| `@RegisterGlobalEvent` | `function` | Registers a global event listener |
| `@RegisterEvent` | `function` | Registers an entity-scoped event listener |

> More annotations are being added (I add them as I go with my mod :D )

---

## Installation

> **Kotlin projects only.** This library uses KSP (Kotlin Symbol Processing) to generate code at compile time. Pure Java projects are not supported.

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
    implementation("io.github.dawidprosba:hytale-aergia-mod-devkit:0.0.2")
    ksp("io.github.dawidprosba:hytale-aergia-mod-devkit:0.0.2")
}

ksp {
    // Package where generated registries will be placed
    arg("registriesOutputPackage", "com.example.exampleplugin.registries.generated")
    // Your mod's main plugin class (used in generated registry bootstrapper)
    arg("pluginClass", "com.example.exampleplugin.ExamplePlugin")
}
```

> Want to build the devkit from source? See [CONTRIBUTING.md](CONTRIBUTING.md).

### Wire up the generated registries

Call each generated registry once in your plugin's `setup()`:

```kotlin
class ExamplePlugin(init: JavaPluginInit) : JavaPlugin(init) {
    override fun setup() {
        ComponentRegistryGenerated.registerAll(
            registry = this.entityStoreRegistry
        )
        SystemRegistryGenerated.registerAll(
            registry = this.entityStoreRegistry
        )
        InteractionRegistryGenerated.registerAll(
            registry = this.getCodecRegistry(Interaction.CODEC)
        )
        GlobalEventRegistryGenerated.registerAll(
            registry = this.eventRegistry
        )
        EventRegistryGenerated.registerAll(
            registry = this.eventRegistry
        )
    }
}
```

Every annotated class or function is now registered automatically.

---

## Usage

### Registering a Component

Annotate your component with `@RegisterComponent` and `@GenerateCodec`. The companion object must implement `CodecProvider` and `ComponentTypeProvider`.

```kotlin
@RegisterComponent("HomingProjectileComponent")
@GenerateCodec
class HomingProjectileComponent : Component<EntityStore> {
    @CodecProperty(documentation = "Enable/Disable Homing Feature")
    var isEnabled = false
    @CodecProperty(documentation = "Maximum distance to search for valid targets when locking on.")
    var homingRange = 0.0
    @CodecProperty(documentation = "How quickly the projectile can turn toward its target each tick, in degrees.")
    var turnRateDegreesPerTick = 0.0
    // ...

    companion object : CodecProvider<HomingProjectileComponent>, ComponentTypeProvider<HomingProjectileComponent> {
        override var componentType: ComponentType<EntityStore, HomingProjectileComponent>? = null
        override val CODEC: BuilderCodec<HomingProjectileComponent> =
            CodecBuilderHomingProjectileComponent
    }
}
```

`CodecBuilderHomingProjectileComponent` is generated at compile time. The name follows the pattern `CodecBuilder<YourClassName>`.

---

### Registering a System

No ID or companion object needed — just annotate the class.

```kotlin
@RegisterSystem()
class HomingMissileTickSystem : EntityTickingSystem<EntityStore>() {
    // ...
}
```

---

### Registering an Interaction

```kotlin
@RegisterInteraction("SimpleProjectileLaunchInteraction")
@GenerateCodec
open class SimpleProjectileLaunchInteraction : SimpleInstantInteraction() {
    companion object : CodecProvider<SimpleProjectileLaunchInteraction> {
        override val CODEC: BuilderCodec<SimpleProjectileLaunchInteraction> =
            CodecBuilderSimpleProjectileLaunchInteraction
    }

    @CodecProperty(documentation = "The projectile to launch.")
    @CodecRequiredValidator
    var projectileId: String = ""
}
```

---

### Registering Event Listeners

Listener functions must be inside a `companion object`. They can optionally accept the event as a parameter.

**Global event listener** — fires regardless of which entity triggers the event:

```kotlin
class ExampleEventListener {
    companion object {
        @RegisterGlobalEvent(PlayerReadyEvent::class)
        fun onPlayerReady(event: PlayerReadyEvent) {
            HytaleLogger.getLogger().atInfo().log("Player is ready: " + event.player.gameMode.name)
        }
    }
}
```

**Entity-scoped event listener** — fires only for the specified subject type:

```kotlin
class ExampleEventListener {
    companion object {
        @RegisterEvent(LoadedAssetsEvent::class, Item::class)
        fun onItemsLoaded(event: LoadedAssetsEvent<String, Item, DefaultAssetMap<String, Item>>) {
            val count = event.loadedAssets.count()
            HytaleLogger.getLogger().atInfo().log("$count items loaded!")
        }
    }
}
```

---

### Generating a Codec

Use `@GenerateCodec` on any class and annotate each field with `@CodecProperty`. Fields must be mutable (`var`). The codec key is derived from the property name with the first letter uppercased (`homingRange` → `"HomingRange"`).

```kotlin
@GenerateCodec
class HomingProjectileComponent : Component<EntityStore> {
    @CodecProperty(documentation = "Enable/Disable Homing Feature")
    var isEnabled = false

    @CodecProperty(documentation = "Maximum distance to search for valid targets when locking on.")
    var homingRange = 0.0

    @CodecProperty(documentation = "The projectile to spawn.", required = false)
    var projectileId: String = ""

    companion object : CodecProvider<HomingProjectileComponent>, ComponentTypeProvider<HomingProjectileComponent> {
        override var componentType: ComponentType<EntityStore, HomingProjectileComponent>? = null
        override val CODEC: BuilderCodec<HomingProjectileComponent> =
            CodecBuilderHomingProjectileComponent
    }
}
```

**Supported primitive types:**

| Kotlin type | Codec |
|---|---|
| `String` | `Codec.STRING` |
| `Int` | `Codec.INT` |
| `Boolean` | `Codec.BOOLEAN` |
| `Float` | `Codec.FLOAT` |
| `Double` | `Codec.DOUBLE` |
| `Long` | `Codec.LONG` |

For any other type, the processor assumes the type has a `CODEC` companion property (e.g. `HomingProjectileDTO.CODEC`).

### Codec Validators

Stack validator annotations on the same field as `@CodecProperty`:

| Annotation | What it generates |
|---|---|
| `@CodecRequiredValidator` | Non-null validator — field must be present and non-null |
| `@CodecProjectileValidator` | Validates the value against the Hytale projectile asset registry |

```kotlin
@CodecProperty(documentation = "The projectile to spawn.")
@CodecRequiredValidator
var projectileId: String = ""
```

---

### Disabling registration

Pass `enabled = false` to any registry annotation to skip registration without removing the code. The runtime logs a warning instead.

```kotlin
@RegisterComponent("HomingProjectileComponent", enabled = false)
@GenerateCodec
class HomingProjectileComponent : Component<EntityStore> { ... }

@RegisterSystem(enabled = false)
class HomingMissileTickSystem : EntityTickingSystem<EntityStore>() { ... }

@RegisterGlobalEvent(PlayerReadyEvent::class, enabled = false)
fun onPlayerReady(event: PlayerReadyEvent) { ... }
```

---

## Documentation

Full documentation is available at **[dawidprosba.github.io/hytale-aergia-mod-devkit/latest/](https://dawidprosba.github.io/hytale-aergia-mod-devkit/latest/)**.

---

<div align="center">
  <sub>Built with coffee and the divine laziness of Aergia.</sub>
</div>
