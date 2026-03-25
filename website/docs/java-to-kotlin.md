---
title: Converting a Hytale Plugin from Java to Kotlin
sidebar_label: Java to Kotlin Migration
sidebar_position: 3
---

# Converting a Hytale Plugin from Java to Kotlin

This guide walks through converting the [Hytale Example Plugin](https://github.com/Kaupenjoe/Hytale-Example-Plugin) boilerplate from Java to Kotlin. If you are starting from that boilerplate, these are the exact steps needed to get a working Kotlin project.

The build system (`build.gradle.kts`) is already written in Kotlin DSL — only the source files and a few build configuration lines need changing.

---

## Prerequisites

- The [Hytale Example Plugin](https://github.com/Kaupenjoe/Hytale-Example-Plugin) boilerplate cloned and building successfully
- IntelliJ IDEA (recommended — it has a built-in Java-to-Kotlin converter)
- Basic familiarity with Kotlin syntax

---

## Overview of Changes

| What | Before | After |
|---|---|---|
| Build plugin | Java only | + `kotlin("jvm")` |
| Toolchain config | `java { toolchain { ... } }` | `kotlin { jvmToolchain(...) }` |
| Source directory | `src/main/java/...` | `src/main/kotlin/...` |
| Source files | `*.java` | `*.kt` |

The `manifest.json`, `gradle.properties`, `settings.gradle.kts`, and all resource files stay exactly the same.

---

## Step 1: Add the Kotlin Plugin to `build.gradle.kts`

Open `build.gradle.kts` and add `kotlin("jvm")` to the `plugins` block. Place it before the `hytale-mod` plugin.

**Before:**
```kotlin
plugins {
    `maven-publish`
    id("hytale-mod") version "0.+"
}
```

**After:**
```kotlin
plugins {
    `maven-publish`
    kotlin("jvm") version "2.3.20"
    id("hytale-mod") version "0.+"
}
```

> The Kotlin JVM plugin automatically adds `kotlin-stdlib` to your compile classpath — you do not need to declare it manually in `dependencies`.

---

## Step 2: Replace the Java Toolchain Block

The `kotlin { jvmToolchain(...) }` extension configures both the Kotlin and Java compilers in one call, so the separate `java { toolchain { ... } }` block is no longer needed.

**Before:**
```kotlin
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }

    withSourcesJar()
}
```

**After:**
```kotlin
kotlin {
    jvmToolchain(javaVersion)
}

java {
    withSourcesJar()
}
```

The `java { withSourcesJar() }` block is kept so Gradle still produces a `-sources.jar` for publishing.

---

## Step 3: Create the Kotlin Source Directory

Create the directory `src/main/kotlin/` mirroring the same package path as your Java sources. In the boilerplate, that is `com/example/exampleplugin/`.

```
src/
  main/
    java/
      com/example/exampleplugin/   <-- old location (to be deleted later)
    kotlin/
      com/example/exampleplugin/   <-- new location
    resources/
```

Gradle's Kotlin JVM plugin automatically picks up sources from `src/main/kotlin`. You do not need to register it manually.

---

## Step 4: Convert `ExamplePlugin.java`

The boilerplate's main plugin class lives at `src/main/java/com/example/exampleplugin/ExamplePlugin.java`.

**Original Java:**
```java
public class ExamplePlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public ExamplePlugin(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));
    }
}
```

**Converted Kotlin (`src/main/kotlin/com/example/exampleplugin/ExamplePlugin.kt`):**
```kotlin
class ExamplePlugin(init: JavaPluginInit) : JavaPlugin(init) {

    companion object {
        private val LOGGER = HytaleLogger.forEnclosingClass()
    }

    init {
        LOGGER.atInfo().log("Hello from %s version %s", name, manifest.version.toString())
    }

    override fun setup() {
        commandRegistry.registerCommand(ExampleCommand(name, manifest.version.toString()))
    }
}
```

**Key differences explained:**

| Java | Kotlin | Why |
|---|---|---|
| `class X extends Y` | `class X(...) : Y(...)` | Constructor parameters and superclass call are combined |
| `static final` field | `companion object { val ... }` | Kotlin has no `static`; companion objects hold class-level state |
| `public ExamplePlugin(...) { super(...); ... }` | `init { ... }` block | Primary constructor calls super automatically; `init` runs after |
| `this.getName()` | `name` | Kotlin maps Java `getX()` getters to properties automatically |
| `this.getManifest()` | `manifest` | Same property mapping |
| `this.getCommandRegistry()` | `commandRegistry` | Same property mapping |

> **Note on `HytaleLogger.forEnclosingClass()`:** This method uses the call stack to detect the calling class. Calling it inside a `companion object` initializer is safe — the enclosing class resolves to `ExamplePlugin`. If you ever see the wrong class name in logs, replace it with `HytaleLogger.forClass(ExamplePlugin::class.java)` as an explicit fallback.

---

## Step 5: Convert `ExampleCommand.java`

The boilerplate's command class lives at `src/main/java/com/example/exampleplugin/ExampleCommand.java`.

**Original Java:**
```java
public class ExampleCommand extends CommandBase {
    private final String pluginName;
    private final String pluginVersion;

    public ExampleCommand(String pluginName, String pluginVersion) {
        super("test", "Prints a test message from the " + pluginName + " plugin.");
        this.setPermissionGroup(GameMode.Adventure);
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        ctx.sendMessage(Message.raw("Hello from the " + pluginName + " v" + pluginVersion + " plugin!"));
    }
}
```

**Converted Kotlin (`src/main/kotlin/com/example/exampleplugin/ExampleCommand.kt`):**
```kotlin
class ExampleCommand(
    private val pluginName: String,
    private val pluginVersion: String
) : CommandBase("test", "Prints a test message from the $pluginName plugin.") {

    init {
        setPermissionGroup(GameMode.Adventure) // Allows the command to be used by anyone, not just OP
    }

    override fun executeSync(ctx: CommandContext) {
        ctx.sendMessage(Message.raw("Hello from the $pluginName v$pluginVersion plugin!"))
    }
}
```

**Key differences explained:**

| Java | Kotlin | Why |
|---|---|---|
| Two separate fields + constructor params | `private val` in primary constructor | Kotlin combines field declaration and constructor parameter into one |
| `super("test", ...)` in constructor body | `: CommandBase("test", ...)` in class header | Superclass constructor call moves to the class declaration |
| `"Hello from " + pluginName + " v" + pluginVersion` | `"Hello from $pluginName v$pluginVersion"` | Kotlin string templates replace concatenation |
| `this.setPermissionGroup(GameMode.Adventure)` | `setPermissionGroup(GameMode.Adventure)` | Only works as a property if there is a matching getter; call the method directly otherwise |
| `@Nonnull CommandContext ctx` | `ctx: CommandContext` | Kotlin types are non-null by default; the annotation is redundant |

---

## Step 6: Delete the Old Java Files

Once the Kotlin files compile (see Step 7), delete the original Java source files:

```
src/main/java/com/example/exampleplugin/ExamplePlugin.java
src/main/java/com/example/exampleplugin/ExampleCommand.java
```

If `src/main/java` is now empty, you can delete the whole directory. Gradle will not complain about a missing `src/main/java` when the Kotlin plugin is present.

---

## Step 7: Verify the Build

Run the Gradle build to confirm everything compiles:

```bash
./gradlew build
```

Or, to run the development server directly:

```bash
./gradlew runServer
```

If the server starts and you see the `Hello from ExamplePlugin version ...` log line, the conversion was successful.

---

## Troubleshooting

**`Unresolved reference` errors on Java getters**
Kotlin automatically maps `getX()` / `setX()` to properties, but this only works when the method follows standard JavaBean naming. If a method does not follow that convention, call it directly: `this.getSomeWeirdName()`.

**`forEnclosingClass()` logs the wrong class name**
Replace the call with an explicit reference:
```kotlin
private val LOGGER = HytaleLogger.forClass(ExamplePlugin::class.java)
```

**`plugin_main_entrypoint` in `gradle.properties`**
This value does not change. Kotlin classes compile to the same JVM class names as Java classes, so `com.example.exampleplugin.ExamplePlugin` remains valid.

**Mixing Java and Kotlin source files**
If you need to keep some Java files during a gradual migration, both `src/main/java` and `src/main/kotlin` can coexist. The Kotlin compiler handles cross-language compilation within the same module automatically.

---

## Final File Structure

```
src/
  main/
    kotlin/
      com/example/exampleplugin/
        ExamplePlugin.kt
        ExampleCommand.kt
    resources/
      manifest.json
      Common/...
      Server/...
build.gradle.kts
gradle.properties
settings.gradle.kts
```
