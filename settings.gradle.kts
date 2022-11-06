pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "Impactor"
include("api")
include("common")
include("game")

//include("bukkit") // Requires > 1.16.5 (No Mojang Mappings available)
include("forge")
include("fabric")
