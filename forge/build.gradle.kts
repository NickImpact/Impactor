plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "0.12.0-SNAPSHOT"
}

architectury {
    platformSetupLoomIde()
    forge()
}

dependencies {
    val loom = project.extensions.getByName<net.fabricmc.loom.api.LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge:forge:${rootProject.property("minecraft")}-${rootProject.property("forge")}")

    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":launcher"))

    modImplementation("ca.landonjw:GooeyLibs:1.16.5-2.3.3-SNAPSHOT")
}

tasks {
    shadowJar {
        dependencies {
            include(project(":common"))
            exclude("forge-client-extra.jar")
        }

        relocate ("io.leangen.geantyref", "net.impactdev.impactor.relocations.geantyref")
        relocate ("org.slf4j", "net.impactdev.impactor.relocations.slf4j")
        relocate ("net.kyori", "net.impactdev.impactor.relocations.kyori")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
        relocate ("org.reflections", "net.impactdev.impactor.relocations.reflections")
        relocate ("ca.landonjw.gooeylibs2", "net.impactdev.impactor.relocations.gooeylibs")
    }

    remapJar {
        archiveBaseName.set("impactor-forge")
        archiveClassifier.set("")
        archiveVersion.set("")
        archiveExtension.set("jarinjar")

        dependsOn(shadowJar)
        inputFile.set(named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").flatMap { it.archiveFile })
    }
}