# @RegisterSystem

Registers an `ISystem<EntityStore>` in the entity store registry. Unlike the other registry annotations, no string ID is required — the class itself is the registry key.

The KSP processor collects all annotated classes and emits `SystemRegistryGenerated`, which you call at plugin startup.

**Target:** `class`
**Retention:** `RUNTIME`

## Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `enabled` | `Boolean` | `true` | Set to `false` to skip registration and log a warning at startup |

## Usage

```kotlin
@RegisterSystem()
class HomingMissileTickSystem : EntityTickingSystem<EntityStore>() {
    // ...
}
```

## Instantiation

The system is instantiated automatically during `registerAll`:

- If the class is a Kotlin `object`, `objectInstance` is used.
- Otherwise, a no-arg constructor is invoked.

No companion object is needed.

## Disabling

Pass `enabled = false` to disable registration without removing the class. The runtime logs a warning instead of registering.

```kotlin
@RegisterSystem(enabled = false)
class HomingMissileTickSystem : EntityTickingSystem<EntityStore>() { ... }
```

## Dependencies

None. `@RegisterSystem` does not require [`@GenerateCodec`](generate-codec.md) because systems are not serialized — they are instantiated directly.

## See also

- [System Registry](../home/registries/system-registry.md) — full setup guide and plugin wiring
