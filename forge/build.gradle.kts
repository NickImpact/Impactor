plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "0.12.0-SNAPSHOT"
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    mixin {
        defaultRefmapName.set("mixins.impactor.forge.refmap.json")
    }
}

dependencies {
    val loom = project.extensions.getByName<net.fabricmc.loom.api.LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge:forge:${rootProject.property("minecraft")}-${rootProject.property("forge")}")

    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":game"))

    modImplementation("ca.landonjw.gooeylibs:forge:3.0.0-1.18.2-SNAPSHOT")
}

tasks {
    jar {
        manifest {
            attributes(
                "MixinConfigs" to "mixins.impactor.forge.json",
                "TweakOrder" to 0,
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker"
            )
        }
    }

    shadowJar {
        dependencies {
            include(project(":api"))
            include(project(":common"))
            include(project(":game"))
            include(dependency("net.impactdev:json:.*"))
            include(dependency("net.kyori:.*:.*"))
            include(dependency("org.spongepowered:math:.*"))
            include(dependency("com.github.ben-manes.caffeine:caffeine:.*"))
            include(dependency("io.leangen.geantyref:geantyref:.*"))
            include(dependency("loom_mappings_1_18_2_layered_hash_40359_v2_forge_1_18_2_40_1_0_forge.ca.landonjw.gooeylibs:fabric:3.0.0-1.18.2-SNAPSHOT"))
            exclude("forge-client-extra.jar")
            exclude("ca/landonjw/gooeylibs2/GooeyLibs.class")
        }

        relocate ("ca.landonjw.gooeylibs2", "net.impactdev.impactor.relocations.gooeylibs")
        relocate ("org.spongepowered.math", "net.impactdev.impactor.relocations.spongepowered.math")
        relocate ("io.leangen.geantyref", "net.impactdev.impactor.relocations.geantyref")
        relocate ("net.kyori", "net.impactdev.impactor.relocations.kyori")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
    }

    remapJar {
        val minecraft = rootProject.property("minecraft")
        val forge = rootProject.property("forge")

        archiveBaseName.set("Impactor-Forge")
        archiveClassifier.set("")
        archiveVersion.set("$minecraft-$forge-${rootProject.version}")

        dependsOn(shadowJar)
        inputFile.set(named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").flatMap { it.archiveFile })
    }

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to rootProject.version)
        }
    }
}