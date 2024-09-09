plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":core"))
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}