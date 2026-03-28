# @RegisterEvent

Registers a function as an entity-scoped event listener. The listener fires only when the given event is triggered for the specified subject type (e.g. a particular `Item` type).

The KSP processor collects all annotated functions and emits `EventRegistryGenerated`, which you call at plugin startup.

**Target:** `function`
**Retention:** `RUNTIME`

## Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `eventClass` | `KClass<*>` | required | The event class to listen for |
| `subject` | `KClass<*>` | required | The entity or item type to scope the listener to |
| `enabled` | `Boolean` | `true` | Set to `false` to skip registration and log a warning at startup |

## Usage

The function must be inside a `companion object`. It can optionally accept the event as a parameter.

=== "With event parameter"
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

=== "Without event parameter"
    ```kotlin
    class ExampleEventListener {
        companion object {
            @RegisterEvent(LoadedAssetsEvent::class, Item::class)
            fun onItemsLoaded() {
                HytaleLogger.getLogger().atInfo().log("Some items were loaded!")
            }
        }
    }
    ```

## Disabling

Pass `enabled = false` to disable registration without removing the function. The runtime logs a warning instead of registering.

```kotlin
@RegisterEvent(LoadedAssetsEvent::class, Item::class, enabled = false)
fun onItemsLoaded() { ... }
```

## Difference from @RegisterGlobalEvent

`@RegisterEvent` requires a `subject` and scopes the listener to that type. Use [`@RegisterGlobalEvent`](register-global-event.md) when the event is not tied to a specific entity or item type.

## Dependencies

None. No codec annotations are required.

## See also

- [`@RegisterGlobalEvent`](register-global-event.md) — global variant with no subject scoping
- [Event Registry](../home/registries/event-registry.md) — full setup guide and plugin wiring
