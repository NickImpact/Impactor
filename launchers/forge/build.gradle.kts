import extensions.writeVersion

plugins {
    id("impactor.launcher-conventions")
    id("impactor.publishing-conventions")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
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

    implementation(project(":minecraft:impl"))
    modImplementation("ca.landonjw.gooeylibs:forge:3.0.0-1.20.1-SNAPSHOT@jar")

    include("io.leangen.geantyref:geantyref:1.3.13")

    modImplementation("net.impactdev.impactor.commands:forge:5.1.1+1.20.1-SNAPSHOT") {
        exclude("net.impactdev.impactor.api", "config")
        exclude("net.impactdev.impactor.api", "core")
        exclude("net.impactdev.impactor.api", "items")
        exclude("net.impactdev.impactor.api", "players")
        exclude("net.impactdev.impactor.api", "plugins")
        exclude("net.impactdev.impactor.api", "storage")
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    shadowJar {
        val mapped = "loom_mappings_1_20_1_layered_hash_40359_v2_forge_1_20_1_47_0_3_forge"
        dependencies {
            include(dependency("$mapped.net.impactdev.impactor.commands:common:.*"))
            include(dependency("$mapped.net.impactdev.impactor.commands:forge:.*"))

            include(dependency("$mapped.ca.landonjw.gooeylibs:forge:.*"))
            include(dependency("cloud.commandframework:cloud-core:.*"))
            include(dependency("cloud.commandframework:cloud-annotations:.*"))
            include(dependency("cloud.commandframework:cloud-brigadier:.*"))
            include(dependency("cloud.commandframework:cloud-services:.*"))
            include(dependency("$mapped.cloud.commandframework:cloud-forge:.*"))
            include(dependency("cloud.commandframework:cloud-minecraft-extras:.*"))

            exclude("forge-client-extra.jar")
            exclude("ca/landonjw/gooeylibs2/forge/GooeyLibs.class")
            exclude("**/PlatformMethods.class")
        }

        val prefix = "net.impactdev.impactor.relocations"
        listOf(
            "ca.landonjw.gooeylibs2",
            "cloud.commandframework",
            "okio",
            "okhttp"
        ).forEach { relocate(it, "$prefix.$it") }

    }

    processResources {
        inputs.property("version", writeVersion())

        filesMatching("META-INF/mods.toml") {
            expand("version" to writeVersion())
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor.launchers"
            artifactId = "forge"
            version = writeVersion()
        }
    }
}

modrinth {
    loaders.set(listOf("forge"))
}