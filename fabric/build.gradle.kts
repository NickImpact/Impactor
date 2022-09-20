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

    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric")}")
    modImplementation("ca.landonjw.gooeylibs:api:3.0.0-SNAPSHOT")
    modImplementation("ca.landonjw.gooeylibs:fabric:3.0.0-SNAPSHOT")

    val FABRIC_API_VERSION = "0.42.0+1.16" // 1.16.5
    setOf(
        "fabric-api-base",
        "fabric-command-api-v1",
        "fabric-lifecycle-events-v1"
    ).forEach { modImplementation(fabricApi.module(it, FABRIC_API_VERSION)) }

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
//            include(dependency("io.github.classgraph:classgraph:.*"))
            include(dependency("loom_mappings_1_16_5_layered_hash_40359_v2.ca.landonjw.gooeylibs:api:3.0.0-SNAPSHOT"))
            include(dependency("loom_mappings_1_16_5_layered_hash_40359_v2.ca.landonjw.gooeylibs:fabric:3.0.0-SNAPSHOT"))
            exclude("ca/landonjw/gooeylibs2/GooeyLibs.class")
        }

//        relocate ("io.github.classgraph", "net.impactdev.impactor.relocations.classgraph")
//        relocate ("nonapi.io.github.classgraph", "net.impactdev.impactor.relocations.classgraph.nonapi")
        relocate ("ca.landonjw.gooeylibs2", "net.impactdev.impactor.relocations.gooeylibs")
        relocate ("ca.landonjw.gooeylibs", "net.impactdev.impactor.relocations.gooeylibs")
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