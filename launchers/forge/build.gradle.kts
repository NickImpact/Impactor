plugins {
    id("impactor.launcher-conventions")
    id("impactor.publishing-conventions")
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    forge {
        runs {
            val client = maybeCreate("client")
            client.vmArgs("-Dmixin.debug.export=true")

            val server = maybeCreate("server")
            server.vmArgs("-Dmixin.debug.export=true")
        }

        mixinConfig("mixins.impactor.forge.json")
    }
}

dependencies {
    forge("net.minecraftforge:forge:${rootProject.property("minecraft")}-${rootProject.property("forge")}")

    implementation(project(":minecraft"))
    modImplementation("ca.landonjw.gooeylibs:forge:3.0.0-1.19.2-SNAPSHOT@jar")

    include("io.leangen.geantyref:geantyref:1.3.13")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
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
        val mapped = "loom_mappings_1_19_2_layered_hash_40359_v2_forge_1_19_2_43_1_47_forge"
        dependencies {
            include(dependency("$mapped.ca.landonjw.gooeylibs:forge:.*"))
            include(dependency("cloud.commandframework:cloud-core:.*"))
            include(dependency("cloud.commandframework:cloud-annotations:.*"))
            include(dependency("cloud.commandframework:cloud-brigadier:.*"))
            include(dependency("cloud.commandframework:cloud-services:.*"))

            exclude("forge-client-extra.jar")
            exclude("ca/landonjw/gooeylibs2/forge/GooeyLibs.class")
        }

        val prefix = "net.impactdev.impactor.relocations"
        listOf(
            "ca.landonjw.gooeylibs2",
            "cloud.commandframework"
        ).forEach { relocate(it, "$prefix.$it") }

    }

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to rootProject.version)
        }
    }
}