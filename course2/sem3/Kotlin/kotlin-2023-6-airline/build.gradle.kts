plugins {
    kotlin("jvm") version "1.9.20"
    application
    id("org.jlleitschuh.gradle.ktlint") version "11.6.0"
}

group = "ru.itmo.ct.kotlin.airline"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

ktlint {
    version = "0.50.0"
}
