import java.nio.file.Files

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://repo.spongepowered.org/repository/maven-releases/")
        maven("https://repo.spongepowered.org/repository/maven-snapshots")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    includeBuild("gradle/build-logic")
}

rootProject.name = "Impactor"

// Impactor Loader
include("loader")

// API Components
include("api:core")
include("api:commands")
include("api:config")
include("api:economy")
include("api:entities")
include("api:events")
include("api:networking")
include("api:permissions")
include("api:platform")
include("api:schedulers")
include("api:storage")
include("api:text")

// Game Level API
//include("game:adventure")
//include("game:items")
//include("game:nbt")

// Launching
include("launchers:fabric")

fun setupProject(project: String) {
    include(project)
    val options = listOf("fabric", "forge", "paper", "velocity")

    options.forEach {
        val path = project(":$project").projectDir.toPath().resolve("launchers").resolve(it)
        if (Files.exists(path)) {
            include("$project:launchers:$it")
        }
    }
}
