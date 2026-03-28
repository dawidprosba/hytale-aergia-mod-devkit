# System Registry

Use `@RegisterSystem`.

## How to register a system

## 1. Annotate your system class

Add `@RegisterSystem()` to your system class. Unlike other registries, no ID is required — the class itself is registered directly.

```kotlin
@RegisterSystem()
class HomingMissileTickSystem : EntityTickingSystem<EntityStore>() {
    //...
}
```

## 2. Disabling a system

Pass `enabled = false` to disable a system without removing it. The code generator will skip registration and log a warning at startup.

```kotlin
@RegisterSystem(enabled = false)
class HomingMissileTickSystem : EntityTickingSystem<EntityStore>() {
    //...
}
```

## Register systems in your plugin

Call `SystemRegistryGenerated.registerAll` in your plugin's `setup` method.

!!! warning
    If `SystemRegistryGenerated` is not available, either the project has not been built yet or the `ksp{}` block in `build.gradle.kts` is misconfigured. Check for compiler errors when running the project.

```kotlin title="ExamplePlugin.kt" hl_lines="3-5" linenums="1"
class ExamplePlugin(init: JavaPluginInit) : JavaPlugin(init) {
    override fun setup() {
        SystemRegistryGenerated.registerAll(
            registry = this.entityStoreRegistry
        )
    }
}
```
