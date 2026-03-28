# Event Registry

Use `@RegisterEvent`.

## How to register an event listener

## 1. Annotate your listener method

Add `@RegisterEvent(EventClass::class, TypeParam::class)` to a method inside a `companion object`. The method must be static — the code generator references it directly. It can either receive the event as a parameter or take no parameters at all.

=== "With event parameter"
    ```kotlin
    class ExampleEventListener {
        companion object {
            @RegisterEvent(LoadedAssetsEvent::class, Item::class)
            fun exampleItemLoadedEventWithParam(event: LoadedAssetsEvent<String, Item, DefaultAssetMap<String, Item>>) {
                val numberOfItems = event.loadedAssets.count()
                HytaleLogger.getLogger().atInfo().log("$numberOfItems items loaded!")
            }
        }
    }
    ```

=== "Without event parameter"
    ```kotlin
    class ExampleEventListener {
        companion object {
            @RegisterEvent(LoadedAssetsEvent::class, Item::class)
            fun exampleItemLoadedEventWithoutParam() {
                HytaleLogger.getLogger().atInfo().log("Some items are loaded!")
            }
        }
    }
    ```

## Register listeners in your plugin

Call `EventRegistryGenerated.registerAll` in your plugin's `setup` method.

!!! warning
    If `EventRegistryGenerated` is not available, either the project has not been built yet or the `ksp{}` block in `build.gradle.kts` is misconfigured. Check for compiler errors when running the project.

```kotlin title="ExamplePlugin.kt" hl_lines="3-5" linenums="1"
class ExamplePlugin(init: JavaPluginInit) : JavaPlugin(init) {
    override fun setup() {
        EventRegistryGenerated.registerAll(
            registry = this.eventRegistry
        )
    }
}
```
