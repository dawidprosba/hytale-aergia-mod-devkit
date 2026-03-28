# Getting Started

This guide walks through adding and configuring the plugin in your project.
If your project is Java project instead of Kotlin, follow this guide [Converting Java Plugin to Kotlin](../../guides/java-to-kotlin) to Convert it into Kotlin.

---

### Add Plugin to Project Dependencies

```kotlin title="build.gradle.kts" hl_lines="3-4"
dependencies {
    // ... your other dependencies
    implementation("io.github.dawidprosba:hytale-aergia-mod-devkit:{{ config.extra.devkit_version }}")
    ksp("io.github.dawidprosba:hytale-aergia-mod-devkit:{{ config.extra.devkit_version }}")
}
```

### Add Configuration For the KSP Processor
Change the `registriesOutputPackage` and `pluginClass` to match your project.

```kotlin title="build.gradle.kts" hl_lines="3-4"
// ...
ksp {
    arg("registriesOutputPackage", "com.example.exampleplugin.registries.generated")
    arg("pluginClass", "com.example.exampleplugin.ExamplePlugin")
}
```

!!! success "Done!"
    You can now start using the devkit in your project.
    On each build It'll generate classes for you.