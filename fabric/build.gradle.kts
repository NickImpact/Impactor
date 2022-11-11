plugins  {
    id("architectury-plugin")
    id("dev.architectury.loom")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    mixin {
        defaultRefmapName.set("mixins.impactor.fabric.refmap.json")
    }

    silentMojangMappingsLicense()
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric")}")
    modImplementation("ca.landonjw.gooeylibs:api:3.0.0-1.19.2-SNAPSHOT")
    modImplementation("ca.landonjw.gooeylibs:fabric:3.0.0-1.19.2-SNAPSHOT")

    val FABRIC_API_VERSION = "0.64.0+1.19.2" // 1.19.2
    setOf(
        "fabric-api-base",
        "fabric-command-api-v2",
        "fabric-lifecycle-events-v1",
        "fabric-networking-api-v1"
    ).forEach { modImplementation(fabricApi.module(it, FABRIC_API_VERSION)) }

    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":game"))
}

tasks {
    shadowJar {
        dependencies {
            include(project(":api"))
            include(project(":common"))
            include(project(":game"))
            include(dependency("net.impactdev:json:.*"))
            include(dependency("net.kyori:.*:.*"))
            include(dependency("org.spongepowered:math:.*"))
            include(dependency("com.github.ben-manes.caffeine:caffeine:.*"))
            include(dependency("io.leangen.geantyref:geantyref:.*"))
//            include(dependency("io.github.classgraph:classgraph:.*"))
            include(dependency("org.spongepowered:configurate-core:.*"))
            include(dependency("org.spongepowered:configurate-gson:.*"))
            include(dependency("org.spongepowered:configurate-yml:.*"))
            include(dependency("org.spongepowered:configurate-hocon:.*"))
            include(dependency("com.zaxxer:HikariCP:.*"))
            include(dependency("com.h2database:h2:.*"))
            include(dependency("mysql:mysql-connector-java:.*"))
            include(dependency("org.mariadb.jdbc:mariadb-java-client:.*"))
            include(dependency("org.mongodb:mongo-java-driver:.*"))
            include(dependency("loom_mappings_1_19_2_layered_hash_40359_v2.ca.landonjw.gooeylibs:api:3.0.0-1.19.2-SNAPSHOT"))
            include(dependency("loom_mappings_1_19_2_layered_hash_40359_v2.ca.landonjw.gooeylibs:fabric:3.0.0-1.19.2-SNAPSHOT"))
            exclude("ca/landonjw/gooeylibs2/fabric/GooeyLibs.class")
        }

//        relocate ("io.github.classgraph", "net.impactdev.impactor.relocations.classgraph")
//        relocate ("nonapi.io.github.classgraph", "net.impactdev.impactor.relocations.classgraph.nonapi")
        relocate ("ca.landonjw.gooeylibs2", "net.impactdev.impactor.relocations.gooeylibs")
        relocate ("ca.landonjw.gooeylibs", "net.impactdev.impactor.relocations.gooeylibs")
        relocate ("org.spongepowered.math", "net.impactdev.impactor.relocations.spongepowered.math")
        relocate ("io.leangen.geantyref", "net.impactdev.impactor.relocations.geantyref")
        relocate ("net.kyori", "net.impactdev.impactor.relocations.kyori")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
        relocate ("org.spongepowered.configurate", "net.impactdev.impactor.relocations.configurate")
        relocate ("com.mongodb", "net.impactdev.impactor.relocations.mongodb")
        relocate ("com.mysql", "net.impactdev.impactor.relocations.mysql")
        relocate ("com.zaxxer.hikari", "net.impactdev.impactor.relocations.hikari")
        relocate ("org.bson", "net.impactdev.impactor.relocations.bson")
        relocate ("org.h2", "net.impactdev.impactor.relocations.h2")
        relocate ("org.mariadb", "net.impactdev.impactor.relocations.mariadb")
    }

    remapJar {
        val minecraft = rootProject.property("minecraft")
        val fabric = rootProject.property("fabric")

        archiveBaseName.set("Impactor-Fabric")
        archiveClassifier.set("")
        archiveVersion.set("$minecraft-$fabric-${rootProject.version}")

        dependsOn(shadowJar)
        inputFile.set(named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").flatMap { it.archiveFile })
    }

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("fabric.mod.json") {
            expand("version" to rootProject.version)
        }
    }
}