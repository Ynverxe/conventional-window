plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.2"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("net.minestom:minestom-snapshots:7ce047b22e")
    compileOnly("io.netty:netty-all:4.1.113.Final")
    implementation(project(":bukkit-platform:nms-common"))
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
}