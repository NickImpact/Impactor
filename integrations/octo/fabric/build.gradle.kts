plugins {
    id("impactor.launcher-conventions")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric-loader")}")
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", rootProject.property("fabric-api").toString()))

    implementation(project(":api:economy"))
    implementation(project(":minecraft:impl"))

    modApi("com.github.ExcessiveAmountsOfZombies:OctoEconomyApi:5137175b1c")
    include("com.github.ExcessiveAmountsOfZombies:OctoEconomyApi:5137175b1c")
}

configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:${rootProject.property("fabric-loader")}")
    }
}

tasks {
    processResources {
        inputs.property("version", writeVersion())

        filesMatching("fabric.mod.json") {
            expand("version" to writeVersion())
        }
    }
}