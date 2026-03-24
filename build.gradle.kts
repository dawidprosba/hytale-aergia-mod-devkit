plugins {
    `maven-publish`
    signing
    id("org.jetbrains.kotlin.jvm") version "2.3.20"
    id("com.gradleup.nmcp") version "1.4.4"
}

group = "io.github.dawidprosba"
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
                url.set("https://github.com/dawidprosba/hytale-aergia-mod-devkit")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("dawidprosba")
                        name.set("Dawid")
                        url.set("https://github.com/dawidprosba")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/dawidprosba/hytale-aergia-mod-devkit.git")
                    developerConnection.set("scm:git:ssh://github.com/dawidprosba/hytale-aergia-mod-devkit.git")
                    url.set("https://github.com/dawidprosba/hytale-aergia-mod-devkit")
                }
            }
        }
    }
}

signing {
    val signingKey = providers.gradleProperty("signing.key")
    val signingPassword = providers.gradleProperty("signing.password")
    if (signingKey.isPresent && signingKey.get().isNotBlank() && signingPassword.isPresent && signingPassword.get().isNotBlank()) {
        useInMemoryPgpKeys(signingKey.get(), signingPassword.get())
        sign(publishing.publications["maven"])
    }
}

nmcp {
    publishAllPublicationsToCentralPortal {
        username.set(providers.gradleProperty("centralPortal.username").getOrElse(""))
        password.set(providers.gradleProperty("centralPortal.password").getOrElse(""))
        publishingType.set("AUTOMATIC")
    }
}