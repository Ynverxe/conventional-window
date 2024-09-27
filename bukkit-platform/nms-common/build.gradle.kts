plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("net.minestom:minestom-snapshots:7ce047b22e")
    compileOnly("io.netty:netty-all:4.1.113.Final")
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
}