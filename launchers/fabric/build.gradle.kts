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
        "fabric-command-api-v1"
    ).forEach { modImplementation(fabricApi.module(it, rootProject.property("fabric-api").toString())) }

    implementation(project(":minecraft"))
    modImplementation("ca.landonjw.gooeylibs:fabric:3.0.0-1.18.2-SNAPSHOT")
    modImplementation("cloud.commandframework:cloud-fabric:1.6.2")

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

}