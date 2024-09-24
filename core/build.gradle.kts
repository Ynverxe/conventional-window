plugins {
    id("java-library")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.minestom:minestom-snapshots:7ce047b22e")
    implementation("org.jetbrains:annotations:24.0.0")
}