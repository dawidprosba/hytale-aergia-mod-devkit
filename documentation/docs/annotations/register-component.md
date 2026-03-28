# @RegisterComponent

Registers a `Component<EntityStore>` subclass in the Hytale component registry. The KSP processor collects all annotated classes and emits `ComponentRegistryGenerated`, which you call at plugin startup.

**Target:** `class`
**Retention:** `RUNTIME`

## Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `id` | `String` | required | The string key under which the component is registered |
| `enabled` | `Boolean` | `true` | Set to `false` to skip registration and log a warning at startup |

## Usage

```kotlin
@RegisterComponent("HomingProjectileComponent")
@GenerateCodec
class HomingProjectileComponent : Component<EntityStore> {
    @CodecProperty(documentation = "Enable/Disable Homing Feature")
    var isEnabled = false

    companion object : CodecProvider<HomingProjectileComponent>, ComponentTypeProvider<HomingProjectileComponent> {
        override var componentType: ComponentType<EntityStore, HomingProjectileComponent>? = null
        override val CODEC: BuilderCodec<HomingProjectileComponent> =
            CodecBuilderHomingProjectileComponent
    }
}
```

## Required companion object

The companion object must implement both `CodecProvider<T>` and `ComponentTypeProvider<T>`:

- `CODEC` — the generated `BuilderCodec<T>` (from [`@GenerateCodec`](generate-codec.md))
- `componentType` — initialized to `null`; auto-populated by the registry during `registerAll`

## Disabling

Pass `enabled = false` to disable registration without removing the class. The runtime logs a warning instead of registering.

```kotlin
@RegisterComponent("HomingProjectileComponent", enabled = false)
@GenerateCodec
class HomingProjectileComponent : Component<EntityStore> { ... }
```

## Dependencies

- [`@GenerateCodec`](generate-codec.md) — required; all components must be serializable
- [`@CodecProperty`](codec-property.md) — required on at least one field for `@GenerateCodec` to produce output

## See also

- [`@GenerateCodec`](generate-codec.md) — generates the codec for the component
- [`@CodecProperty`](codec-property.md) — marks fields for serialization
- [Component Registry](../home/registries/component-registry.md) — full setup guide and plugin wiring
