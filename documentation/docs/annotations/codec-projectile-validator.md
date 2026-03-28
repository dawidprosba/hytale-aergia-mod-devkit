# @CodecProjectileValidator

Adds the Hytale projectile asset validator to the codec entry for the annotated property. The validator resolves the projectile asset reference against the asset cache using `Projectile.VALIDATOR_CACHE.getValidator().late()`.

**Target:** `property`, `value parameter`
**Retention:** `SOURCE` (compile-time only)

## Parameters

None.

## Usage

Place on the same property as [`@CodecProperty`](codec-property.md) when the field holds a projectile asset identifier that must be validated against the loaded asset cache.

```kotlin
@GenerateCodec
class HomingProjectileComponent : Component<EntityStore> {
    @CodecProperty(documentation = "The projectile asset to home toward.")
    @CodecProjectileValidator
    var projectileId: String = ""

    companion object : CodecProvider<HomingProjectileComponent>, ComponentTypeProvider<HomingProjectileComponent> {
        override var componentType: ComponentType<EntityStore, HomingProjectileComponent>? = null
        override val CODEC: BuilderCodec<HomingProjectileComponent> =
            CodecBuilderHomingProjectileComponent
    }
}
```

## Generated output

The annotation causes the following to be appended to the codec entry:

```kotlin
.addValidator(Projectile.VALIDATOR_CACHE.getValidator().late())
```

The `.late()` call defers validator resolution until the asset cache is populated, which is why this is safe to use with fields referencing runtime-loaded assets.

## Dependencies

- The containing class must be annotated with [`@GenerateCodec`](generate-codec.md).
- The property must be annotated with [`@CodecProperty`](codec-property.md).

## See also

- [`@CodecProperty`](codec-property.md) — marks the field for codec inclusion
- [`@CodecRequiredValidator`](codec-required-validator.md) — alternative validator for non-null checks
- [`@GenerateCodec`](generate-codec.md) — triggers codec generation for the class
