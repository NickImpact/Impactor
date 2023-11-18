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
    fabric()
}

repositories {
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric-loader")}")
    listOf(
        "fabric-lifecycle-events-v1",
        "fabric-command-api-v2",
        "fabric-networking-api-v1",
    ).forEach { modImplementation(fabricApi.module(it, rootProject.property("fabric-api").toString())) }

    implementation(project(":minecraft:impl"))
    modImplementation("ca.landonjw.gooeylibs:fabric:3.0.0-1.20.1-SNAPSHOT@jar")

    modImplementation("net.impactdev.impactor.commands:fabric:5.1.1+1.20.1-SNAPSHOT") {
        exclude("net.impactdev.impactor.api", "config")
        exclude("net.impactdev.impactor.api", "core")
        exclude("net.impactdev.impactor.api", "items")
        exclude("net.impactdev.impactor.api", "players")
        exclude("net.impactdev.impactor.api", "plugins")
        exclude("net.impactdev.impactor.api", "storage")
    }

    listOf(
        libs.cloudAnnotations,
        libs.cloudMinecraftExtras,
        libs.cloudFabric
    ).forEach { include(it) }

    modCompileOnly("eu.pb4:placeholder-api:2.0.0-pre.1+1.19.2")
    include("io.leangen.geantyref:geantyref:1.3.13")

    modRuntimeOnly("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    processResources {
        inputs.property("version", writeVersion())

        filesMatching("fabric.mod.json") {
            expand("version" to writeVersion())
        }
    }

    shadowJar {
        val mapped = "loom_mappings_1_20_1_layered_hash_40359_v2"
        dependencies {
            include(dependency("net.impactdev.impactor.commands:common:.*"))
            include(dependency("$mapped.net.impactdev.impactor.commands:fabric:.*"))

            include(dependency("org.apache.maven:maven-artifact:.*"))
            include(dependency("$mapped.ca.landonjw.gooeylibs:fabric:.*"))

            exclude("**/PlatformMethods.class")
        }

        val prefix = "net.impactdev.impactor.relocations"
        listOf(
            "org.apache.maven",
            "ca.landonjw.gooeylibs2",
            "okio",
            "okhttp"
        ).forEach { relocate(it, "$prefix.$it") }
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor.launchers"
            artifactId = "fabric"
            version = writeVersion()
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
    }
}