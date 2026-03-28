# @CodecRequiredValidator

Adds a non-null validator (`Validators.nonNull()`) to the codec entry for the annotated property. The validator runs during deserialization and rejects null values.

**Target:** `property`, `value parameter`
**Retention:** `SOURCE` (compile-time only)

## Parameters

None.

## Usage

Place on the same property as [`@CodecProperty`](codec-property.md). The validator is appended after the `.documentation(...)` call in the generated codec chain.

```kotlin
@GenerateCodec
open class SimpleProjectileLaunchInteraction : SimpleInstantInteraction() {
    @CodecProperty(documentation = "The projectile to launch.")
    @CodecRequiredValidator
    var projectileId: String = ""

    companion object : CodecProvider<SimpleProjectileLaunchInteraction> {
        override val CODEC: BuilderCodec<SimpleProjectileLaunchInteraction> =
            CodecBuilderSimpleProjectileLaunchInteraction
    }
}
```

## Generated output

The annotation causes the following to be appended to the codec entry:

```kotlin
.addValidator(Validators.nonNull())
```

## Dependencies

- The containing class must be annotated with [`@GenerateCodec`](generate-codec.md).
- The property must be annotated with [`@CodecProperty`](codec-property.md).

## See also

- [`@CodecProperty`](codec-property.md) — marks the field for codec inclusion
- [`@CodecProjectileValidator`](codec-projectile-validator.md) — alternative validator for projectile asset references
- [`@GenerateCodec`](generate-codec.md) — triggers codec generation for the class
