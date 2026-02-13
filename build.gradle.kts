plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    application
}

kotlin{
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.12")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.12")
    implementation("io.ktor:ktor-server-html-builder-jvm:2.3.12")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.12")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.12")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")

    // Fix for "Failed to load JUnit Platform" on some Gradle setups
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}

application {
    // Kotlin top-level main function in app/Main.kt
    mainClass.set("app.MainKt")
}


