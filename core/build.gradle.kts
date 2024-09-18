plugins {
    id("java-library")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    api("net.minestom:minestom-snapshots:7ce047b22e")
    implementation("org.jetbrains:annotations:24.0.0")

    testImplementation("net.minestom:minestom-snapshots:7ce047b22e")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    test {
        useJUnitPlatform()
    }
}