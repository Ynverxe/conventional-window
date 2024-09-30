plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
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
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("io.netty:netty-all:4.1.113.Final")
    compileOnly("net.minestom:minestom-snapshots:7ce047b22e")
    implementation(project(":core"))
    implementation(project(":bukkit-platform:nms-common"))
    implementation(project(":bukkit-platform:nms-1.20"))
    implementation(project(":bukkit-platform:nms-1.20.5"))
    implementation(project(":bukkit-platform:nms-1.21"))
}