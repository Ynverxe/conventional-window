plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
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
    // Cannot download 1.20.5 for any reason
    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}