plugins {
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

minecraft {
    version("1.18.2")
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
    api(project(":game"))

    implementation("org.spongepowered:spongeapi:9.0.0")
}

tasks {
    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("META-INF/sponge_plugins.json") {
            expand("version" to rootProject.version)
        }
    }
}