plugins {
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "2.3.20"
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.github.dawidprosba"
val baseVersion = "0.0.2"
version = if (providers.gradleProperty("local").isPresent) "$baseVersion-SNAPSHOT" else baseVersion

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


mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(group.toString(), "hytale-aergia-mod-devkit", version.toString())
    pom {
        name = "Hytale Aergia Mod Devkit"
        description =
            "KSP annotation processors for Hytale mod development: codec generation and auto-registration of components, interactions, and systems."
        url = "https://github.com/dawidprosba/hytale-aergia-mod-devkit"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                id = "dawidprosba"
                name = "Dawid"
                url = "https://github.com/dawidprosba"
            }
        }
        scm {
            url = "https://github.com/dawidprosba/hytale-aergia-mod-devkit"
            connection =
                "scm:git:git://github.com/dawidprosba/hytale-aergia-mod-devkit.git"
            url =
                "scm:git:ssh://git@github.com/dawidprosba/hytale-aergia-mod-devkit.git"
        }
    }
}

//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            artifactId = "hytale-aergia-mod-devkit"
//            from(components["java"])
//
//        }
//    }
//}