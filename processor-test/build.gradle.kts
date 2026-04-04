plugins {
    kotlin("jvm") version "2.3.20"
    id("com.google.devtools.ksp") version "2.3.6"
}

repositories {
    google()
    mavenCentral()
}

ksp {
    arg("registriesOutputPackage", "test.generated")
    arg("pluginClass", "test.TestPlugin")
}

dependencies {
    ksp(project(":"))
    compileOnly(project(":"))
    compileOnly(files(providers.gradleProperty("hytale.serverJar").get()))

    testImplementation(kotlin("test"))
    testCompileOnly(project(":"))
    testCompileOnly(files(providers.gradleProperty("hytale.serverJar").get()))
}

// KSP does not clean up META-INF/services files between incremental builds,
// so the same file appears twice when processResources runs. EXCLUDE keeps
// the first copy (from the previous KSP run) which is identical anyway.
tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
    filter {
        includeTestsMatching("test.CodecGenerationTest")
        includeTestsMatching("test.RegistryGenerationTest")
    }
    systemProperty(
        "ksp.generated.sources",
        layout.buildDirectory.dir("generated/ksp/main/kotlin").get().asFile.absolutePath
    )
}

kotlin {
    jvmToolchain(25)
}