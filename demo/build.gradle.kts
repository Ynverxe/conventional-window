plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":core"))

    implementation("net.kyori:adventure-api:4.5.0")
}