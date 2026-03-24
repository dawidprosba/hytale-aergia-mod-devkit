# Contributing

## Prerequisites

- JDK 25
- The Hytale server jar — set the path in `gradle.properties`:

```properties
hytale.serverJar=/path/to/hytale-server.jar
```

See `gradle.properties.example` for reference.

## Building and publishing locally

Clone the repository, set `hytale.serverJar` in `gradle.properties`, then publish with the `-Plocal` flag:

```bash
./gradlew publishToMavenLocal -Plocal
```

The `-Plocal` flag appends `-SNAPSHOT` to the version, so the local build is versioned `0.0.1-SNAPSHOT` and never conflicts with the published release `0.0.1`.

This installs the artifact to your local Maven cache (`~/.m2`):

```
~/.m2/repository/io/github/dawidprosba/hytale-aergia-mod-devkit/0.0.1-SNAPSHOT/
```

## Using a local build in your mod project

In your mod's `build.gradle.kts`, add `mavenLocal()` **before** `mavenCentral()` and use the `-SNAPSHOT` version:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.github.dawidprosba:hytale-aergia-mod-devkit:0.0.1-SNAPSHOT")
    ksp("io.github.dawidprosba:hytale-aergia-mod-devkit:0.0.1-SNAPSHOT")
}
```

> When you're done testing local changes, remove `mavenLocal()` and switch back to the released version (`0.0.1`) so your project resolves against Maven Central.

## Making changes

After editing the devkit source, re-run `./gradlew publishToMavenLocal -Plocal` and trigger a clean build in your mod project to pick up the updated artifact:

```bash
./gradlew clean build
```