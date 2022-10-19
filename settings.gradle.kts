pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://repo.spongepowered.org/repository/maven-releases/")
        maven("https://repo.spongepowered.org/repository/maven-snapshots")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "Impactor"
include("api")
include("common")
//include("bukkit") // Requires > 1.16.5 (No Mojang Mappings available)
include("forge")
include("fabric")
//include("sponge:common")
//include("sponge:sf")
//include("sponge:sv")
include("game")
