plugins {
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

minecraft {
    version("1.16.5")
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-snapshots") {
        name = "Sponge Snapshots"
    }
    maven("https://repo.spongepowered.org/repository/maven-releases") {
        name = "Sponge Releases"
    }
}

dependencies {
    api(project(":api"))
    api(project(":common"))

    implementation("org.spongepowered:spongeapi:8.1.0")
}