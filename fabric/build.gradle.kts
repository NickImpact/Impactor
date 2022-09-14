plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "0.12.0-SNAPSHOT"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    mavenCentral()
}

dependencies {
    val loom = project.extensions.getByName<net.fabricmc.loom.api.LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    minecraft("com.mojang:minecraft:1.16.5")//${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric")}")

    implementation(project(":api"))
    implementation(project(":common"))
}

tasks {
    shadowJar {
        dependencies {
            include(project(":api"))
            include(project(":common"))
            include(dependency("net.impactdev:json:.*"))
            include(dependency("net.kyori:.*:.*"))
            include(dependency("org.spongepowered:math:.*"))
            include(dependency("com.github.ben-manes.caffeine:caffeine:.*"))
            include(dependency("io.leangen.geantyref:geantyref:.*"))
        }

        relocate ("org.spongepowered", "net.impactdev.impactor.relocations.spongepowered")
        relocate ("io.leangen.geantyref", "net.impactdev.impactor.relocations.geantyref")
        relocate ("net.kyori", "net.impactdev.impactor.relocations.kyori")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
    }

    remapJar {
        val minecraft = rootProject.property("minecraft")
        val fabric = rootProject.property("fabric")

        archiveBaseName.set("Impactor-Fabric")
        archiveClassifier.set("")
        archiveVersion.set("$minecraft-$fabric-${rootProject.version}")

        dependsOn(shadowJar)
        inputFile.set(named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").flatMap { it.archiveFile })
    }

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("fabric.mod.json") {
            expand("version" to rootProject.version)
        }
    }
}