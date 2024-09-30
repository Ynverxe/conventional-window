pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "conventional-window"

include("core")
include("item-lib")
include("demo")
include("bukkit-platform")
include("bukkit-platform:nms-1.20")
findProject(":bukkit-platform:nms-1.20")?.name = "nms-1.20"
include("bukkit-platform:nms-1.20.5")
findProject(":bukkit-platform:nms-1.20.5")?.name = "nms-1.20.5"
include("bukkit-platform:nms-common")
findProject(":bukkit-platform:nms-common")?.name = "nms-common"
include("bukkit-platform:nms-1.21")
findProject(":bukkit-platform:nms-1.21")?.name = "nms-1.21"
