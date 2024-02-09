plugins {
    kotlin("jvm") version "1.9.10"
    application
    id("org.jlleitschuh.gradle.ktlint") version "11.6.0"
}

group = "ru.itmo.ct.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.5.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}

ktlint {
    version = "0.50.0"
}
