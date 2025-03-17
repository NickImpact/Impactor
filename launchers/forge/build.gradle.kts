import extensions.writeVersion

plugins {
    id("impactor.launcher-conventions")
    id("impactor.publishing-conventions")
}

loom {
    neoForge {
        runs {
            val client = maybeCreate("client")
            client.vmArgs("-Dmixin.debug.export=true")

            val server = maybeCreate("server")
            server.vmArgs("-Dmixin.debug.export=true")
        }
    }
}

repositories {
    maven(url = "https://maven.neoforged.net/releases")
}

dependencies {
    neoForge(libs.neoforge)

    implementation(project(":minecraft:impl"))
    include(modImplementation("ca.landonjw.gooeylibs:api:3.1.0-1.21.1-SNAPSHOT")!!)

    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")

    include("io.leangen.geantyref:geantyref:1.3.13")

    include(modImplementation("net.impactdev.impactor.commands:neoforge:5.3.1+1.21.1") {
        exclude("net.impactdev.impactor.api", "config")
        exclude("net.impactdev.impactor.api", "core")
        exclude("net.impactdev.impactor.api", "items")
        exclude("net.impactdev.impactor.api", "players")
        exclude("net.impactdev.impactor.api", "plugins")
        exclude("net.impactdev.impactor.api", "storage")
    })

    listOf(
        libs.cloudAnnotations,
        libs.cloudConfirmations,
        libs.cloudProcessorsCommon,
        libs.cloudMinecraftExtras,
    ).forEach { include(it) }

    include(modImplementation("net.kyori:adventure-platform-neoforge:6.0.0")!!)

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    shadowJar {
        dependencies {
            val remap = "loom_mappings_1_21_1_layered_hash_40359_v2_neoforge_21_1_66_forge"

            include(dependency("$remap.net.impactdev.impactor.commands:common:.*"))
            exclude("**/PlatformMethods.class")
            exclude("**/client-extra.jar")
            exclude("**/mappings.tiny")
        }
    }

    processResources {
        inputs.property("version", writeVersion(true))

        filesMatching("META-INF/neoforge.mods.toml") {
            expand("version" to writeVersion(true))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            artifact(tasks.remapProductionJar)

            groupId = "net.impactdev.impactor.launchers"
            artifactId = "neoforge"
            version = writeVersion(true)
        }
    }
}

modrinth {
    loaders.set(listOf("neoforge"))
}