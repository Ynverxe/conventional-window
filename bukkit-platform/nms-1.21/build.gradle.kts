plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.2"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    compileOnly("net.minestom:minestom-snapshots:7ce047b22e")
    compileOnly("io.netty:netty-all:4.1.113.Final")
    compileOnly(project(":bukkit-platform:nms-common"))
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}