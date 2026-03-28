# Converting a Hytale Plugin from Java to Kotlin

This guide walks through converting
the [Hytale Example Plugin](https://github.com/Kaupenjoe/Hytale-Example-Plugin) boilerplate from
Java to Kotlin. If you are starting from that boilerplate, these are the exact steps needed to get a
working Kotlin project.

The build system (`build.gradle.kts`) is already written in Kotlin DSL — only the source files and a
few build configuration lines need changing.

---

## Prerequisites

- The [Hytale Example Plugin](https://github.com/Kaupenjoe/Hytale-Example-Plugin) boilerplate cloned
  and building successfully or similarly configured project.
- IntelliJ IDEA (recommended — it has a built-in Java-to-Kotlin converter)
- Basic familiarity with Kotlin syntax

---

## 1. Add Kotlin to the Plugins

Lets start by adding Kotlin support into the project.
(When you add Kotlin support, you can have both Java and Kotlin source files in the same project, so
you can convert them one at a time and mod will work.)

```kotlin title="build.gradle.kts"
plugins {
    `maven-publish`
    // In your IntelliJ do no not forget to click sync gradle project.
    // add-next-line
    kotlin("jvm") version "2.3.20" // Latest version as of time of writing this.
    id("hytale-mod") version "0.+"
}

group = "com.example"
version = "0.1.0"
val javaVersion = 25

dependencies {
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.jspecify)
}

hytale {
    // uncomment if you want to add the Assets.zip file to your external libraries;
    // ⚠️ CAUTION, this file is very big and might make your IDE unresponsive for some time!
    //
    // addAssetsDependency = true

    // uncomment if you want to develop your mod against the pre-release version of the game.
    //
    // updateChannel = "pre-release"
}

// add-next-line
kotlin {
    // add-next-line
    jvmToolchain(javaVersion)
// add-next-line
}

java {
    // remove-next-line
    toolchain {
        // remove-next-line
        languageVersion = JavaLanguageVersion.of(javaVersion)
        // remove-next-line
    }
    withSourcesJar()
}
// --- Rest of your build.gradle.kts ---
```

## 2. Convert ExamplePlugin (Your plugin class into a Kotlin class)

> **Note:** The Kotlin files are created under `src/main/kotlin/` where java files are `src/main/java/`.
> So if your plugin is in `src/main/java/com/example/plugin/ExamplePlugin.java`, create a new file in
> `src/main/kotlin/com/example/plugin/ExamplePlugin.kt`.

> **Tip:** In IntelliJ IDEA, use **Code > Convert Java File to Kotlin File** to automatically convert your
> existing Java file, or simply paste Java code into a `.kt` file and IntelliJ will offer to convert
> it for you. Afterwards, delete the original `.java` file.

**Before (Java)**

```java title="src/main/java/com/example/plugin/ExamplePlugin.java"
package com.example.exampleplugin;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

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

**After (Kotlin)**

```kotlin title="src/main/kotlin/com/example/plugin/ExamplePlugin.kt"
package com.example.exampleplugin

import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit

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

## Q&A

**Can I convert only some of my Java files to Kotlin?**

Yes, you can have both Java and Kotlin files in the same project.

**Can I have two versions of the same file, one for example `Test.java` and one for `Test.kt`?**

No, this will likely result in a compilation error because of the name clash.

---

Java Examples used in this guide were taken from the [Hytale Example Plugin](https://github.com/Kaupenjoe/Hytale-Example-Plugin).
