pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }

    includeBuild("build-logic")
}

rootProject.name = "Impactor"
include("api:core")
include("api:config")
include("api:commands")
include("api:economy")
include("api:items")
include("api:players")
include("api:plugins")
include("api:storage")
include("api:text")
include("api:ui")

include("impactor")
include("game")
include("launchers")
include("launchers:forge")
//include("launchers:fabric")
