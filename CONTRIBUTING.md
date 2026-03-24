# Contributing

## Prerequisites

- JDK 25
- The Hytale server jar — set the path in `gradle.properties`:

```properties
hytale.serverJar=/path/to/hytale-server.jar
```

See `gradle.properties.example` for reference.

## Building and publishing locally

Clone the repository, set `hytale.serverJar` in `gradle.properties`, then run:

```bash
./gradlew publishToMavenLocal
```

This installs the artifact to your local Maven cache (`~/.m2`):

```
~/.m2/repository/io/github/dawidprosba/hytale-aergia-mod-devkit/1.0.0/
```

## Using a local build in your mod project

In your mod's `build.gradle.kts`, add `mavenLocal()` **before** `mavenCentral()` so Gradle picks up the local build first:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.github.dawidprosba:hytale-aergia-mod-devkit:1.0.0")
    ksp("io.github.dawidprosba:hytale-aergia-mod-devkit:1.0.0")
}
```

> When you're done testing local changes, remove `mavenLocal()` so your project resolves against the published release.

## Making changes

After editing the devkit source, re-run `./gradlew publishToMavenLocal` and trigger a clean build in your mod project to pick up the updated artifact:

```bash
./gradlew clean build
```