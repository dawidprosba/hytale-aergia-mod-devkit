# @RegisterGlobalEvent

Registers a function as a global event listener. Global listeners fire for the given event regardless of which entity or subject triggers it.

The KSP processor collects all annotated functions and emits `GlobalEventRegistryGenerated`, which you call at plugin startup.

**Target:** `function`
**Retention:** `RUNTIME`

## Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `eventClass` | `KClass<*>` | required | The event class to listen for |
| `enabled` | `Boolean` | `true` | Set to `false` to skip registration and log a warning at startup |

## Usage

The function must be inside a `companion object`. It can optionally accept the event as a parameter.

=== "With event parameter"
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

=== "Without event parameter"
    ```kotlin
    class ExampleEventListener {
        companion object {
            @RegisterGlobalEvent(PlayerReadyEvent::class)
            fun onPlayerReady() {
                HytaleLogger.getLogger().atInfo().log("Player is ready!")
            }
        }
    }
    ```

## Disabling

Pass `enabled = false` to disable registration without removing the function. The runtime logs a warning instead of registering.

```kotlin
@RegisterGlobalEvent(PlayerReadyEvent::class, false)
fun onPlayerReady(event: PlayerReadyEvent) { ... }
```

## Difference from @RegisterEvent

`@RegisterGlobalEvent` does not require a subject — it fires for all instances. Use [`@RegisterEvent`](register-event.md) when you need to scope the listener to a specific entity or item type.

## Dependencies

None. No codec annotations are required.

## See also

- [`@RegisterEvent`](register-event.md) — entity-scoped variant that also requires a `subject` parameter
- [Global Event Registry](../home/registries/global-event-registry.md) — full setup guide and plugin wiring
