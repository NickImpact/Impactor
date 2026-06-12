import extensions.writeVersion

plugins {
    id("impactor.launcher-conventions")
    id("impactor.publishing-conventions")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
}

dependencies {
    implementation("net.fabricmc:fabric-loader:${rootProject.property("fabric-loader")}")
    listOf(
        "fabric-lifecycle-events-v1",
        "fabric-command-api-v2",
        "fabric-networking-api-v1",
    ).forEach { implementation(fabricApi.module(it, rootProject.property("fabric-api").toString())) }

    implementation(project(":minecraft:impl"))
    include(implementation("com.github.ApolloNetworkMC.GooeyLibs:fabric:26.1.2-SNAPSHOT")!!)
    include(implementation("net.impactdev.impactor.commands:fabric:5.3.1+1.21.1") {
        exclude("net.impactdev.impactor.api", "config")
        exclude("net.impactdev.impactor.api", "core")
        exclude("net.impactdev.impactor.api", "items")
        exclude("net.impactdev.impactor.api", "players")
        exclude("net.impactdev.impactor.api", "plugins")
        exclude("net.impactdev.impactor.api", "storage")
    })

    listOf(
        libs.cloudAnnotations,
        libs.cloudMinecraftExtras,
        libs.cloudConfirmations,
        libs.cloudProcessorsCommon,
    ).forEach { include(it) }

    implementation(libs.adventurePlatformModShared)
    include(libs.adventurePlatformModShared)

    include(implementation("eu.pb4:placeholder-api:3.0.0+26.1")!!)
    include("io.leangen.geantyref:geantyref:1.3.13")

    runtimeOnly("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    processResources {
        inputs.property("version", writeVersion(true))

        filesMatching("fabric.mod.json") {
            expand("version" to writeVersion(true))
        }
    }

    shadowJar {
        mergeServiceFiles()
        dependencies {
            exclude("**/PlatformMethods.class")
            exclude("**/mappings.tiny")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            artifact(tasks.shadowJar)

            groupId = "net.impactdev.impactor.launchers"
            artifactId = "fabric"
            version = writeVersion(true)
        }
    }
}

modrinth {
    loaders.set(listOf("fabric"))
    dependencies {
        required.project("fabric-api")
        optional.project("placeholder-api")
    }
}

configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:${rootProject.property("fabric-loader")}")
        force("com.google.code.gson:gson:2.10.1")
    }
}