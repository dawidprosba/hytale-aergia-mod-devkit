# Annotations

Aergia Mod Devkit provides two sets of annotations: **codec annotations** that generate serialization codecs at compile time, and **registry annotations** that auto-register mod elements at startup.

## Codec annotations

| Annotation | Target | Description |
|---|---|---|
| [`@GenerateCodec`](generate-codec.md) | `class` | Generates a `BuilderCodec` for the annotated class |
| [`@CodecProperty`](codec-property.md) | `property`, `value parameter` | Marks a field for inclusion in the generated codec |
| [`@CodecRequiredValidator`](codec-required-validator.md) | `property`, `value parameter` | Adds a non-null validator to a codec field |
| [`@CodecProjectileValidator`](codec-projectile-validator.md) | `property`, `value parameter` | Adds the Hytale projectile asset validator to a codec field |

## Registry annotations

| Annotation | Target | Description |
|---|---|---|
| [`@RegisterComponent`](register-component.md) | `class` | Registers a component in the Hytale component registry |
| [`@RegisterInteraction`](register-interaction.md) | `class` | Registers an interaction in the codec map registry |
| [`@RegisterSystem`](register-system.md) | `class` | Registers a system in the entity store registry |
| [`@RegisterGlobalEvent`](register-global-event.md) | `function` | Registers a global event listener |
| [`@RegisterEvent`](register-event.md) | `function` | Registers an entity-scoped event listener |
