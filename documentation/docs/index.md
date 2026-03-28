# Aergia Mod Devkit
This is a devkit library that eliminates boilerplate code. Use annotations to automatically generate component, system registration, and CodecBuilder etc...

---

<div class="grid cards" markdown>

-   :material-clock-fast:{ .lg .middle } __Set up in 5 minutes__

    ---

    Add library into your project dependencies, and add ksp configuration!

    [:octicons-arrow-right-24: Getting started](./home/getting-started)

-   :material-language-kotlin:{ .lg .middle } __It's made for Kotlin__

    ---

    Library is made for Kotlin Plugins/Mods and should work with any Server version.


-   :fontawesome-brands-java:{ .lg .middle } __Java plugin to Kotlin Guide__

    ---

    Guide helping you to migrate your Java plugin to Kotlin.

    [:octicons-arrow-right-24: Java to Kotlin Guide](./guides/java-to-kotlin)

-   :material-scale-balance:{ .lg .middle } __Open Source, MIT__

    ---

    This project is fully OpenSource, Feel free to __contribute__ or fork and make your own version.
    [:octicons-arrow-right-24: Contributing](https://github.com/dawidprosba/hytale-aergia-mod-devkit?tab=contributing-ov-file)
</div>

# Codec Generation
Writing Codec is boring and looks ugly. Takes a lot of space in the code and it's not very readable.
Look at this example, where I wanted to create a component that holds information for Homing Projectile behavior.

To properly serialize/deserialize this component, I need to write a Codec for it, and it looks like this: 
### Before Using the library
!!! example "Before Using the library"
    ```java title="HomingProjectileComponent.java"  linenums="1" hl_lines="2-45"
    public class HomingProjectileComponent implements Component<EntityStore> {
        public static final BuilderCodec<HomingProjectileComponent> CODEC = BuilderCodec
                .builder(HomingProjectileComponent.class, HomingProjectileComponent::new)
                .append(new KeyedCodec<>("IsEnabled", Codec.BOOLEAN, true),
                        (homingProjectileConfig, value) -> homingProjectileConfig.isEnabled = value,
                        homingProjectileConfig -> homingProjectileConfig.isEnabled
                )
                .documentation("Enable/Disable Homing Feature")
                .add()
                .append(
                        new KeyedCodec<>("HomingRange", Codec.DOUBLE, true),
                        (homingProjectileConfig, value) -> homingProjectileConfig.homingRange = value,
                        homingProjectileConfig -> homingProjectileConfig.homingRange
                )
                .documentation("Maximum distance to search for valid targets when locking on.")
                .add()
                .append(
                        new KeyedCodec<>("TurnRateDegreesPerTick", Codec.DOUBLE, true),
                        (homingProjectileConfig, value) -> homingProjectileConfig.turnRateDegreesPerTick = value,
                        homingProjectileConfig -> homingProjectileConfig.turnRateDegreesPerTick
                )
                .documentation("How quickly the projectile can turn toward its target each tick, in degrees.")
                .add()
                .append(
                        new KeyedCodec<>("LockConeAngleDegrees", Codec.DOUBLE, true),
                        (homingProjectileConfig, value) -> homingProjectileConfig.lockConeAngleDegrees = value,
                        homingProjectileConfig -> homingProjectileConfig.lockConeAngleDegrees
                )
                .documentation("Half-angle of the forward lock cone, in degrees, used to decide which targets can be acquired.")
                .add()
                .append(
                        new KeyedCodec<>("ChaseRange", Codec.DOUBLE, true),
                        (homingProjectileConfig, value) -> homingProjectileConfig.chaseRange = value,
                        homingProjectileConfig -> homingProjectileConfig.chaseRange
                )
                .documentation("Distance at which the projectile switches to direct chase and snaps to the target instead of steering smoothly.")
                .add()
                .append(
                        new KeyedCodec<>("ChaseSpeedBonus", Codec.DOUBLE, true),
                        (homingProjectileConfig, value) -> homingProjectileConfig.chaseSpeedBonus = value,
                        homingProjectileConfig -> homingProjectileConfig.chaseSpeedBonus
                )
                .documentation("Extra speed applied while the projectile is in the direct-chase zone.")
                .add()
                .build();
    
        private boolean isEnabled;
        private double homingRange;
        private double turnRateDegreesPerTick;
        private double lockConeAngleDegrees;
        private double chaseRange;
        private double chaseSpeedBonus;
        
        // Constructors etc..
    }
    ```

As you can see its a lot of lines of code. It takes a lot of mental energy to write this and have this chunk of code in your class. And if you want to reference this component in e.g your interaction
you'll need to write CODEC again! Thats where the devkit comes in to help you not think about that. Just using few annotations, the code is generated automatically with as less lines of code as possible for you!
### After Using the library
!!! example
```kotlin title="HomingProjectileComponent.kt" linenums="1" hl_lines="11 13-24 30-32"
package com.example.exampleplugin.components

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.GenerateCodec
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.api.CodecProvider


@GenerateCodec  // (1)!
class HomingProjectileComponent : Component<EntityStore> {
    @CodecProperty(documentation = "Enable/Disable Homing Feature")  // (2)!
    var isEnabled = false
    @CodecProperty(documentation = "Maximum distance to search for valid targets when locking on.")
    var homingRange = 0.0
    @CodecProperty(documentation = "How quickly the projectile can turn toward its target each tick, in degrees.")
    var turnRateDegreesPerTick = 0.0
    @CodecProperty(documentation = "Half-angle of the forward lock cone, in degrees, used to decide which targets can be acquired.")
    var lockConeAngleDegrees = 0.0
    @CodecProperty(documentation = "Distance at which the projectile switches to direct chase and snaps to the target instead of steering smoothly.")
    var chaseRange = 0.0
    @CodecProperty(documentation = "Extra speed applied while the projectile is in the direct-chase zone.")
    var chaseSpeedBonus = 0.0

    
    // Constructors etc..
    

    companion object : CodecProvider<HomingProjectileComponent> {  // (3)!
        override val CODEC: BuilderCodec<HomingProjectileComponent> = CodecBuilderHomingProjectileComponent
    }
}
```

1.  Let generator know that this class needs CODEC generation.
2.  Let generator know that this property is part of the CODEC. The generator will automatically pickup its type! (Yes, there's option to add validators too! I'll talk about it in section dedicated to this.)
3.  Use CodecProvider from `io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.api.CodecProvider` and register the CODEC by using generated CODEC. The naming convention is `CodecBuilder<YourComponentName>`, It'll be generated at compile time so you need to run your build. (I'll automate that in the future)

Your codec is generated at Compile Time, meaning you need to compile your code to generate the codec (I want to make this automatic in the future). And then import `CodecBuilderHomingProjectileComponent`.
You can see how it got generated (But remember to not edit it as it will get overwritten on next compilation):

??? abstract "Generated Codec"
    ```kotlin title="CodecBuilderHomingProjectileComponent.kt" linenums="1"
    // Auto-generated by hytale-aergia-mod-devkit. Changes here will be overwritten, do not edit manually.
    package com.example.exampleplugin.components
    
    import com.hypixel.hytale.codec.Codec
    import com.hypixel.hytale.codec.KeyedCodec
    import com.hypixel.hytale.codec.builder.BuilderCodec
    
    public val CodecBuilderHomingProjectileComponent: BuilderCodec<HomingProjectileComponent> =
        BuilderCodec.builder(HomingProjectileComponent::class.java, ::HomingProjectileComponent)
        .append(KeyedCodec("IsEnabled", Codec.BOOLEAN, false),
            { config, value -> config.isEnabled = value },
            { config -> config.isEnabled }
        )
        .documentation("Enable/Disable Homing Feature")
        .add()
        .append(KeyedCodec("HomingRange", Codec.DOUBLE, false),
            { config, value -> config.homingRange = value },
            { config -> config.homingRange }
        )
        .documentation("Maximum distance to search for valid targets when locking on.")
        .add()
        .append(KeyedCodec("TurnRateDegreesPerTick", Codec.DOUBLE, false),
            { config, value -> config.turnRateDegreesPerTick = value },
            { config -> config.turnRateDegreesPerTick }
        )
        .documentation("How quickly the projectile can turn toward its target each tick, in degrees.")
        .add()
        .append(KeyedCodec("LockConeAngleDegrees", Codec.DOUBLE, false),
            { config, value -> config.lockConeAngleDegrees = value },
            { config -> config.lockConeAngleDegrees }
        )
        .documentation("Half-angle of the forward lock cone, in degrees, used to decide which targets can be acquired.")
        .add()
        .append(KeyedCodec("ChaseRange", Codec.DOUBLE, false),
            { config, value -> config.chaseRange = value },
            { config -> config.chaseRange }
        )
        .documentation("Distance at which the projectile switches to direct chase and snaps to the target instead of steering smoothly.")
        .add()
        .append(KeyedCodec("ChaseSpeedBonus", Codec.DOUBLE, false),
            { config, value -> config.chaseSpeedBonus = value },
            { config -> config.chaseSpeedBonus }
        )
        .documentation("Extra speed applied while the projectile is in the direct-chase zone.")
        .add()
        .build()
    ```

# Auto Registration
The library also provides automatic registration of Components, Systems and Events (I'm working to extend that to handle all cases). 

Lets build on our example from CODEC generation. We want to make so the component is automatically registered when the plugin is loaded without any additional code.

```kotlin title="HomingProjectileComponent.kt" linenums="1" hl_lines="1 6-9"
// You can pass false as second argument to disable registration of this component!
@RegisterComponent("HomingProjectileComponent") // (1)!
@GenerateCodec
class HomingProjectileComponent : Component<EntityStore> {
    // ... CODE
    companion object : CodecProvider<HomingProjectileComponent>, ComponentTypeProvider<HomingProjectileComponent> { // (2)!
        override val CODEC: BuilderCodec<HomingProjectileComponent> = CodecBuilderHomingProjectileComponent
        override var componentType: ComponentType<EntityStore, HomingProjectileComponent>? = null
    }
}
```

1.  Use `@RegisterComponent` annotation to register the component. The name you provide in the annotation will be used as the component name in the game. You can reference this component in your interactions or other places using this name.
2. The `companion object` is required to provide the `CODEC` property. Use CodecBuilder<YourCompnentClass> to make sure the CODEC value is resolved correctly.

This part will generate registration code for you, but we need to hook the registry in our plugin `setup()` method. (This is one time config only as all components will be appended to the generated registry).

```kotlin title="ExamplePlugin.kt" linenums="1" hl_lines="4-6"
class ExamplePlugin(init: JavaPluginInit) : JavaPlugin(init) {
    // ... CODE
    override fun setup() {
        ComponentRegistryGenerated.registerAll( // (1)!
            registry = this.entityStoreRegistry
        )
    }
    // ... CODE
}
```

1. Just need to add call to the generated registry.

!!! abstract "Generated Registry"
    This is how the registry will look like after the registration. If you disable component registration, It'll convert into LOG block and will print information to console.
    Remember you do not need nor you should edit this file as it will get overwritten on next compilation.
    All components annotated with @RegisterComponent will be automatically added to this registry!
    ```kotlin
    public object ComponentRegistryGenerated {
      public val LOGGER: HytaleLogger = HytaleLogger.get(ExamplePlugin::class.simpleName)
    
      public fun registerAll(registry: ComponentRegistryProxy<EntityStore>) {
        registerComponent(HomingProjectileComponent::class, registry)
      }
    }
    ```