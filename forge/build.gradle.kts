plugins {
    id("architectury-plugin")
    id("dev.architectury.loom")
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

    modImplementation("ca.landonjw.gooeylibs:api:3.0.0-1.19.2-SNAPSHOT")
    modImplementation("ca.landonjw.gooeylibs:forge:3.0.0-1.19.2-SNAPSHOT")
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
            include(dependency("com.typesafe:config:.*"))
            include(dependency("org.spongepowered:configurate-core:.*"))
            include(dependency("org.spongepowered:configurate-gson:.*"))
            include(dependency("org.spongepowered:configurate-yml:.*"))
            include(dependency("org.spongepowered:configurate-hocon:.*"))
            include(dependency("com.zaxxer:HikariCP:.*"))
            include(dependency("com.h2database:h2:.*"))
            include(dependency("mysql:mysql-connector-java:.*"))
            include(dependency("org.mariadb.jdbc:mariadb-java-client:.*"))
            include(dependency("org.mongodb:mongo-java-driver:.*"))
            include(dependency("loom_mappings_1_19_2_layered_hash_40359_v2_forge_1_19_2_43_1_47_forge.ca.landonjw.gooeylibs:api:3.0.0-1.19.2-SNAPSHOT"))
            include(dependency("loom_mappings_1_19_2_layered_hash_40359_v2_forge_1_19_2_43_1_47_forge.ca.landonjw.gooeylibs:forge:3.0.0-1.19.2-SNAPSHOT"))

            exclude("forge-client-extra.jar")
            exclude("ca/landonjw/gooeylibs2/forge/GooeyLibs.class")
        }

        relocate ("ca.landonjw.gooeylibs2", "net.impactdev.impactor.relocations.gooeylibs")
        relocate ("org.spongepowered.math", "net.impactdev.impactor.relocations.spongepowered.math")
        relocate ("io.leangen.geantyref", "net.impactdev.impactor.relocations.geantyref")
        relocate ("net.kyori", "net.impactdev.impactor.relocations.kyori")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
        relocate ("org.spongepowered.configurate", "net.impactdev.impactor.relocations.configurate")
        relocate ("com.mongodb", "net.impactdev.impactor.relocations.mongodb")
        relocate ("com.mysql", "net.impactdev.impactor.relocations.mysql")
        relocate ("com.zaxxer.hikari", "net.impactdev.impactor.relocations.hikari")
        relocate ("org.bson", "net.impactdev.impactor.relocations.bson")
        relocate ("org.h2", "net.impactdev.impactor.relocations.h2")
        relocate ("org.mariadb", "net.impactdev.impactor.relocations.mariadb")
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