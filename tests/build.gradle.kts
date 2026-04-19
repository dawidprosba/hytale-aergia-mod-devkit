import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    testRuntimeOnly(project(":"))
    testCompileOnly(files(providers.gradleProperty("hytale.serverJar").get()))
    testRuntimeOnly(files(providers.gradleProperty("hytale.serverJar").get()))
    testImplementation("io.mockk:mockk:1.14.9")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")

}

tasks.test {
    useJUnitPlatform()
    filter {
        includeTestsMatching("test.api.*")
    }
    systemProperty(
        "ksp.generated.sources",
        layout.buildDirectory.dir("generated/ksp/main/kotlin").get().asFile.absolutePath
    )
}

kotlin {
    jvmToolchain(25)
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}