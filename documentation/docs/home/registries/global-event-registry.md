# Global Event Registry

Registers all global events in the project.

# How to register a global event listener

## 1. Annotate your listener method

Add `@RegisterGlobalEvent(EventClass::class)` to a method inside a `companion object`. The method must be static — the code generator references it directly. It can either receive the event as a parameter or take no parameters at all.

=== "With event parameter"
    ```kotlin
    class ExampleEventListener {
        companion object {
            @RegisterGlobalEvent(PlayerReadyEvent::class)
            fun examplePlayerReadyEventWithParam(event: PlayerReadyEvent) {
                HytaleLogger.getLogger().atInfo().log("Player is ready with gamemode -> " + event.player.gameMode.name)
            }
        }
    }
    ```

=== "Without event parameter"
    ```kotlin
    class ExampleEventListener {
        companion object {
            @RegisterGlobalEvent(PlayerReadyEvent::class)
            fun examplePlayerReadyEventWithoutParam() {
                HytaleLogger.getLogger().atInfo().log("Player is ready!")
            }
        }
    }
    ```

## 2. Disabling a listener

Pass `false` as the second argument to disable a listener without removing it. The code generator will skip registration and log a warning at startup.

```kotlin
@RegisterGlobalEvent(PlayerReadyEvent::class, false)
fun examplePlayerReadyEventWithParam(event: PlayerReadyEvent) {
    HytaleLogger.getLogger().atInfo().log("Player is ready with gamemode -> " + event.player.gameMode.name)
}
```

# Register listeners in your plugin

Call `GlobalEventRegistryGenerated.registerAll` in your plugin's `setup` method.

!!! warning
    If `GlobalEventRegistryGenerated` is not available, either the project has not been built yet or the `ksp{}` block in `build.gradle.kts` is misconfigured. Check for compiler errors when running the project.

```kotlin title="ExamplePlugin.kt" hl_lines="3-5" linenums="1"
class ExamplePlugin(init: JavaPluginInit) : JavaPlugin(init) {
    override fun setup() {
        GlobalEventRegistryGenerated.registerAll(
            registry = this.eventRegistry
        )
    }
}
```
