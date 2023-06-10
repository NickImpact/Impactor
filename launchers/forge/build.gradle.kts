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
        mixinConfig("mixins.impactor.forge.commands.json")
    }
}

dependencies {
    forge("net.minecraftforge:forge:${rootProject.property("minecraft")}-${rootProject.property("forge")}")

    implementation(project(":minecraft:impl"))
    modImplementation("ca.landonjw.gooeylibs:forge:3.0.0-1.19.2-SNAPSHOT@jar")

    include("io.leangen.geantyref:geantyref:1.3.13")

    modImplementation("net.impactdev.impactor.commands:forge:5.0.0+1.19.2-SNAPSHOT") {
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
        val mapped = "loom_mappings_1_19_2_layered_hash_40359_v2_forge_1_19_2_43_1_47_forge"
        dependencies {
            include(dependency("$mapped.net.impactdev.impactor.commands:common:.*"))
            include(dependency("$mapped.net.impactdev.impactor.commands:forge:.*"))

            include(dependency("$mapped.ca.landonjw.gooeylibs:forge:.*"))
            include(dependency("cloud.commandframework:cloud-core:.*"))
            include(dependency("cloud.commandframework:cloud-annotations:.*"))
            include(dependency("cloud.commandframework:cloud-brigadier:.*"))
            include(dependency("cloud.commandframework:cloud-services:.*"))
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

            val plugin = rootProject.property("plugin").toString()
            val minecraft = rootProject.property("minecraft").toString()
            val snapshot = rootProject.property("snapshot") == "true"

            version = "${plugin}+${minecraft}"
            if(snapshot) {
                version += "-SNAPSHOT"
            }
        }
    }
}

fun writeVersion(): String
{
    val plugin = rootProject.property("plugin")
    val minecraft = rootProject.property("minecraft")
    val snapshot = rootProject.property("snapshot") == "true"

    var version = "$plugin+$minecraft"
    if(snapshot) {
        version = "$version-SNAPSHOT"
    }

    return version
}