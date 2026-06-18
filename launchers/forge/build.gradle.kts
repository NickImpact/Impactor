import extensions.writeVersion

plugins {
    id("impactor.launcher-conventions")
    id("impactor.publishing-conventions")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    neoForge {
        runs {
            val client = maybeCreate("client")
            client.jvmArguments.add("-Dmixin.debug.export=true")

            val server = maybeCreate("server")
            server.jvmArguments.add("-Dmixin.debug.export=true")
        }
    }
}

repositories {
    maven(url = "https://maven.neoforged.net/releases")
    mavenLocal()
}

dependencies {
    neoForge(libs.neoforge)

    implementation(project(":minecraft:impl"))
    include(implementation("com.github.ApolloNetworkMC.GooeyLibs:neoforge:26.1.2-SNAPSHOT")!!)
    implementation("com.github.ApolloNetworkMC.GooeyLibs:api:26.1.2-SNAPSHOT")

    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")

    include("io.leangen.geantyref:geantyref:1.3.13")

    include(implementation("net.impactdev.impactor.commands:neoforge:5.3.1+26.1.2") {
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

    include(implementation("net.kyori:adventure-platform-neoforge:6.9.0")!!)

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
        val minecraft: String = rootProject.property("minecraft") as String
        val neoforge: String = rootProject.property("neoforge") as String

        inputs.property("version", writeVersion(true))
        inputs.property("minecraft", minecraft)
        inputs.property("neoforge", neoforge)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(
                "version" to writeVersion(true),
                "minecraft" to minecraft,
                "neoforge" to neoforge
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            artifact(tasks.shadowJar)

            groupId = "net.impactdev.impactor.launchers"
            artifactId = "neoforge"
            version = writeVersion(true)
        }
    }
}

modrinth {
    loaders.set(listOf("neoforge"))
}