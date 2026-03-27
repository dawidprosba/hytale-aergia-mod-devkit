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

tasks.test {
    useJUnitPlatform()
    filter {
        includeTestsMatching("test.CodecGenerationTest")
    }
    systemProperty(
        "ksp.generated.sources",
        layout.buildDirectory.dir("generated/ksp/main/kotlin").get().asFile.absolutePath
    )
}

kotlin {
    jvmToolchain(25)
}