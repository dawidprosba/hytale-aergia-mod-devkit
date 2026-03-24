# Contributing

## Building from source

Clone the repository and publish to Maven Local:

```bash
./gradlew publishToMavenLocal
```

The library will be available at `com.dcbd.hytale:hytale-aergia-mod-devkit:1.0.0` in your local Maven cache.
Make sure `mavenLocal()` is included in your mod project's repositories block.

## Prerequisites

- JDK 25
- The Hytale server jar — set the path in `gradle.properties`:

```properties
hytale.serverJar=/path/to/hytale-server.jar
```

See `gradle.properties.example` for reference.