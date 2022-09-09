pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
    }
}

rootProject.name = "Impactor"
include("api")
include("common")
include("launcher")
include("forge")
//include("forge:forge-platform")
include("forge:forge-launcher")
//include("bukkit")
