# Registering the registry in Plugin class
Add the `ComponentRegistryGenerated.registerAll` call to the `setup` method of your plugin.
If the class isn't available, you either didn't build/run the project or have misconfigured the `ksp{}` section in `build.gradle.kts` (Also check if you have any errors when running the project.).
```kotlin title="ExamplePlugin.kt" hl_lines="3-5" linenums="1"
class ExamplePlugin(init: JavaPluginInit) : JavaPlugin(init) {
    override fun setup() {
        ComponentRegistryGenerated.registerAll(
            registry = this.entityStoreRegistry
        )
    }
}
```

# How to register components
## 1. Use annotation, that will let the compiler know to generate the code for your component and register it in the registry.
To register a component, you need to annotate it with `@RegisterComponent("<id-of-your-component>")` and `@GenerateCodec` as each component needs to be
serializable.

!!! example
    ```kotlin
    @RegisterComponent("HomingProjectileComponent")
    @GenerateCodec
    class HomingProjectileComponent : Component<EntityStore> {
    //...
    }
    ```
## 2. Companion configuration (static values)
Companion must implement `CodecProvider<ChangeWithYourComponent>, ComponentTypeProvider<ChangeWithYourComponent>` both interfaces.
This makes so we need to provide:

- `CODEC` - codec for your component

- `componentType` - type of your component (we will set it to null, during rgistration that field will auto-populate).

```kotlin
@RegisterComponent("HomingProjectileComponent")
class HomingProjectileComponent : Component<EntityStore> {
    companion object : ComponentTypeProvider<HomingProjectileComponent> {
        override var componentType: ComponentType<EntityStore, HomingProjectileComponent>? = null
        override val CODEC: BuilderCodec<HomingProjectileComponent> = 
            CodecBuilderHomingProjectileComponent // (1)!
    }
}
```

1. This will be resolvable after you run the compiler. The name will be generated like `CodecBuilder<YourComponentClassName>`

## 3. Mark fields to add them to the codec
To make field serializable (and thus add them to the codec), you need to mark them with `@CodecField` annotation.
Remember to make the variable mutable (`var`).
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
    //... rest of the code
}
```