plugins {
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "2.3.20"
}

group = "com.dcbd.hytale"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.3.6")
    implementation("com.squareup:kotlinpoet:2.2.0")
    compileOnly(files(providers.gradleProperty("hytale.serverJar").get()))
}

kotlin {
    jvmToolchain(25)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "hytale-aergia-mod-devkit"
            from(components["java"])
            pom {
                name.set("Hytale Aergia Mod Devkit")
                description.set("KSP annotation processors for Hytale mod development: codec generation and auto-registration of components, interactions, and systems.")
            }
        }
    }
}