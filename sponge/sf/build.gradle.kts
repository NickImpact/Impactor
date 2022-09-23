plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "0.12.0-SNAPSHOT"
}

architectury {
    platformSetupLoomIde()
    forge()
}

repositories {
    mavenCentral()
}

dependencies {
    val loom = project.extensions.getByName<net.fabricmc.loom.api.LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge:forge:${rootProject.property("minecraft")}-${rootProject.property("forge")}")

    implementation(project(":sponge:common"))
}

tasks {
    shadowJar {
        dependencies {
            include(project(":api"))
            include(project(":common"))
            include(project(":game"))
            include(project(":sponge:common"))
            include(dependency("net.kyori:event-api:.*"))
            include(dependency("net.impactdev:json:.*"))
//            include(dependency("io.github.classgraph:classgraph:.*"))
            include(dependency("com.github.ben-manes.caffeine:caffeine:.*"))
            exclude("forge-client-extra.jar")
        }

//        relocate ("io.github.classgraph", "net.impactdev.impactor.relocations.classgraph")
//        relocate ("nonapi.io.github.classgraph", "net.impactdev.impactor.relocations.classgraph.nonapi")
        relocate ("net.kyori.event", "net.impactdev.impactor.relocations.kyori.event")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
    }

    remapJar {
        val minecraft = rootProject.property("minecraft")
        val sponge = rootProject.property("sponge")

        archiveBaseName.set("Impactor-SpongeForge")
        archiveClassifier.set("")
        archiveVersion.set("$minecraft-$sponge-${rootProject.version}")

        dependsOn(shadowJar)
        inputFile.set(named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").flatMap { it.archiveFile })
    }
}