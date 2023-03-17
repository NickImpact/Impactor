plugins {
    id("impactor.launcher-conventions")
    id("impactor.publishing-conventions")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric-loader")}")
    listOf(
        "fabric-lifecycle-events-v1",
        "fabric-command-api-v2",
        "fabric-networking-api-v1"
    ).forEach { modImplementation(fabricApi.module(it, rootProject.property("fabric-api").toString())) }

    implementation(project(":minecraft"))
    modImplementation("ca.landonjw.gooeylibs:fabric:3.0.0-1.19.2-SNAPSHOT@jar")

    include("cloud.commandframework:cloud-annotations:1.7.1")
    include("cloud.commandframework:cloud-fabric:1.7.1")
    modImplementation("cloud.commandframework:cloud-fabric:1.7.1") {
        exclude("net.fabricmc.fabric-api")
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("fabric.mod.json") {
            expand("version" to rootProject.version)
        }
    }

    shadowJar {
        val mapped = "loom_mappings_1_19_2_layered_hash_40359_v2"
        dependencies {
            include(dependency("org.apache.maven:maven-artifact:.*"))
            include(dependency("$mapped.ca.landonjw.gooeylibs:fabric:.*"))
        }

        val prefix = "net.impactdev.impactor.relocations"
        listOf(
            "org.apache.maven",
            "ca.landonjw.gooeylibs2"
        ).forEach { relocate(it, "$prefix.$it") }

    }
}