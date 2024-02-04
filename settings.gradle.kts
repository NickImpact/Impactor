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

    includeBuild("build-logic")
}

plugins {
    id("ca.stellardrift.polyglot-version-catalogs") version "6.1.0"
}

rootProject.name = "Impactor"

include("api:core")
include("api:config")
include("api:economy")
include("api:items")
include("api:mail")
include("api:players")
include("api:plugins")
include("api:storage")
include("api:text")
include("api:translations")
include("api:ui")

include("impactor")
include("minecraft:api")
include("minecraft:impl")
include("launchers:forge")
include("launchers:fabric")

// Integrations
include("integrations:vault")
