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

## Overview
To convert a Hytale plugin from Java to Kotlin, you need to add Kotlin support into your project and
convert your Java files into Kotlin files. (You can do it gradually, one file at a time, and project will still work with mixed Java and Kotlin files.)

## Adding kotlin support to the project

### Step 1: Check what files are in the project
Check if you have `build.gradle.kts`, `settings.gradle.kts` or `build.gradle` and `settings.gradle` file.
!!! note

    If you have `build.gradle.kts` and `settings.gradle.kts` file, you can skip this step.

To convert it to kotlin setting files, you can follow this [Migrating from Groovy to Kotlin](https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html) guide or use AI to handle migration for you.
At the end of the guide, is example `build.gradle.kts` and `settings.gradle.kts` file if you want to check it out.

### Step 2: Add Kotlin plugin and Configure kotlin compiler
```kotlin linenums="1" hl_lines="3 27-29" title="build.gradle.kts"
plugins {
    `maven-publish`
    kotlin("jvm") version "2.3.20" // (1)!
    id("hytale-mod") version "0.+" 
}

group = "com.example"
version = "0.1.0"
val javaVersion = 25

dependencies {
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.jspecify)
}

hytale { // (2)!
    // uncomment if you want to add the Assets.zip file to your external libraries;
    // ⚠️ CAUTION, this file is very big and might make your IDE unresponsive for some time!
    //
    // addAssetsDependency = true

    // uncomment if you want to develop your mod against the pre-release version of the game.
    //
    // updateChannel = "pre-release"
}

kotlin {
    jvmToolchain(javaVersion) // (3)!
}

java {
    withSourcesJar()
}

// .. rest of your settings.gradle.kts
```

1.  Add kotlin plugin into your plugins block. At the time of writing this guide, latest stable version of Kotlin is `2.30.20`. The rest of the plugins are Plugin specific.
2.  This is configuration for `hytale-mod` plugin, if you don't use it, then you don't need to have it. 
3.  This is configuration for kotlin compiler, it tells it to compile to the same java version as the rest of the project. Hytale Server uses Java 25, so this should be set to `25` Look line `:9`
----

### Step 3: Convert Java Plugin file to Kotlin
!!! info

    The Kotlin files are created under `src/main/kotlin/` where java files are `src/main/java/`.
    So if your plugin is in `src/main/java/com/example/plugin/ExamplePlugin.java`, create a new file in
    `src/main/kotlin/com/example/plugin/ExamplePlugin.kt`.

!!! tip

    In IntelliJ IDEA, use **Code > Convert Java File to Kotlin File** to automatically convert your
    existing Java file, or simply paste Java code into a `.kt` file and IntelliJ will offer to convert
    it for you. Afterwards, delete the original `.java` file to avoid conflicts.

=== "Example Java Plugin"
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

=== "Example Kotlin Plugin"
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
            LOGGER.atInfo().log("Hello from %s version %s using KOTLIN!", name, manifest.version.toString())
        }
    
        override fun setup() {
            commandRegistry.registerCommand(ExampleCommand(name, manifest.version.toString()))
        }
    }
    ```

### Run the Server
You don't need to change rest of the files for now and project should run smoothly.
After you run server you should look for `Hello from ExamplePlugin version 0.1.0 using KOTLIN!` in console.

!!! example
    
    ```bash
    ... OTHER LOGS
    [ExamplePlugin] Hello from ExampleGroup:ExamplePlugin version 0.1.0 using KOTLIN!
    ... OTHER LOGS
    ```

### Example configuration
!!! info
    The configuration is heavily inspired by [Example Plugin](https://github.com/Kaupenjoe/Hytale-Example-Plugin), the template is already configured with good practices
    and uses `build.gradle.kts` and `settings.gradle.kts`, all you need to do is to add Kotlin support to your project and convert your Java files into Kotlin files.
=== "build.gradle.kts"
    ```kotlin title="build.gradle.kts"
    plugins {
        `maven-publish`
        kotlin("jvm") version "2.3.20"
        id("hytale-mod") version "0.+"
    }
    
    group = "com.example"
    version = "0.1.0"
    val javaVersion = 25
    
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://central.sonatype.com/repository/maven-snapshots/") {
            name = "SonatypeCentral"
        }
        maven("https://maven.hytale-modding.info/releases") {
            name = "HytaleModdingReleases"
        }
    }
    
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
    
    kotlin {
        jvmToolchain(javaVersion)
    }
    
    java {
        withSourcesJar()
    }
    
    tasks.named<ProcessResources>("processResources") {
        var replaceProperties = mapOf(
            "plugin_group" to findProperty("plugin_group"),
            "plugin_maven_group" to project.group,
            "plugin_name" to project.name,
            "plugin_version" to project.version,
            "server_version" to findProperty("server_version"),
    
            "plugin_description" to findProperty("plugin_description"),
            "plugin_website" to findProperty("plugin_website"),
    
            "plugin_main_entrypoint" to findProperty("plugin_main_entrypoint"),
            "plugin_author" to findProperty("plugin_author")
        )
    
        filesMatching("manifest.json") {
            expand(replaceProperties)
        }
    
        inputs.properties(replaceProperties)
    }
    
    tasks.withType<Jar> {
        manifest {
            attributes["Specification-Title"] = rootProject.name
            attributes["Specification-Version"] = version
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] =
                providers.environmentVariable("COMMIT_SHA_SHORT")
                    .map { "${version}-${it}" }
                    .getOrElse(version.toString())
        }
    }
    
    publishing {
        repositories {
            // This is where you put repositories that you want to publish to.
            // Do NOT put repositories for your dependencies here.
        }
    
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
    
    // IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
    idea {
        module {
            isDownloadSources = true
            isDownloadJavadoc = true
        }
    }
    
    val syncAssets = tasks.register<Copy>("syncAssets") {
        group = "hytale"
        description = "Automatically syncs assets from Build back to Source after server stops."
    
        // Take from the temporary build folder (Where the game saved changes)
        from(layout.buildDirectory.dir("resources/main"))
    
        // Copy into your actual project source (Where your code lives)
        into("src/main/resources")
    
        // IMPORTANT: Protect the manifest template from being overwritten
        exclude("manifest.json")
    
        // If a file exists, overwrite it with the new version from the game
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    
        doLast {
            println("✅ Assets successfully synced from Game to Source Code!")
        }
    }
    
    afterEvaluate {
        // Now Gradle will find it, because the plugin has finished working
        val targetTask = tasks.findByName("runServer") ?: tasks.findByName("server")
    
        if (targetTask != null) {
            targetTask.finalizedBy(syncAssets)
            logger.lifecycle("✅ specific task '${targetTask.name}' hooked for auto-sync.")
        } else {
            logger.warn("⚠️ Could not find 'runServer' or 'server' task to hook auto-sync into.")
        }
    }
    ```
=== "settings.gradle.kts"
    ```kotlin title="settings.gradle.kts"
    pluginManagement {
        repositories {
            gradlePluginPortal()
            mavenCentral()
            maven("https://maven.hytale-modding.info/releases") {
                name = "HytaleModdingReleases"
            }
        }
    }
    
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    }
    
    rootProject.name = "ExamplePlugin"
    ```
