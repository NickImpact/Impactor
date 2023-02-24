plugins {
    id("impactor.launcher-conventions")
    id("impactor.publishing-conventions")
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

    implementation(project(":minecraft"))
    "developmentForge"(project(":minecraft")) {
        isTransitive = false
    }
    "developmentForge"("ca.landonjw.gooeylibs:api:3.0.0-SNAPSHOT")
    modImplementation("ca.landonjw.gooeylibs:forge:3.0.0-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    jar {
        manifest {
            attributes(
                "MixinConfigs" to "mixins.impactor.forge.json",
                "TweakOrder" to 0,
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker"
            )
        }
    }

    // TODO - With 1.18+, switch to using JarInJar system instead to avoid relocation
    shadowJar {
        dependencies {
            include(dependency("net.kyori:.*:.*"))
            include(dependency("org.spongepowered:math:.*"))
            include(dependency("com.github.ben-manes.caffeine:caffeine:.*"))
            include(dependency("io.leangen.geantyref:geantyref:.*"))
            include(dependency("io.github.classgraph:classgraph:.*"))

            include(dependency("com.typesafe:config:.*"))
            include(dependency("org.spongepowered:configurate-core:.*"))
            include(dependency("org.spongepowered:configurate-gson:.*"))
            include(dependency("org.spongepowered:configurate-yml:.*"))
            include(dependency("org.spongepowered:configurate-hocon:.*"))

            include(dependency("org.spongepowered:math:.*"))

            include(dependency("com.zaxxer:HikariCP:.*"))
            include(dependency("com.h2database:h2:.*"))
            include(dependency("mysql:mysql-connector-java:.*"))
            include(dependency("org.mariadb.jdbc:mariadb-java-client:.*"))
            include(dependency("org.mongodb:mongo-java-driver:.*"))

            include(dependency("cloud.commandframework:cloud-core:.*"))
            include(dependency("cloud.commandframework:cloud-annotations:.*"))
            include(dependency("cloud.commandframework:cloud-brigadier:.*"))
            include(dependency("cloud.commandframework:cloud-services:.*"))

            exclude("forge-client-extra.jar")
            exclude("ca/landonjw/gooeylibs2/forge/GooeyLibs.class")
        }

        relocate ("net.kyori", "net.impactdev.impactor.relocations.kyori")
        relocate ("org.spongepowered.math", "net.impactdev.impactor.relocations.spongepowered.math")
        relocate ("io.leangen.geantyref", "net.impactdev.impactor.relocations.geantyref")
        relocate ("io.github.classgraph", "net.impactdev.impactor.relocations.classgraph")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
        relocate ("org.spongepowered.configurate", "net.impactdev.impactor.relocations.configurate")
        relocate ("com.mongodb", "net.impactdev.impactor.relocations.mongodb")
        relocate ("com.mysql", "net.impactdev.impactor.relocations.mysql")
        relocate ("com.zaxxer.hikari", "net.impactdev.impactor.relocations.hikari")
        relocate ("org.bson", "net.impactdev.impactor.relocations.bson")
        relocate ("org.h2", "net.impactdev.impactor.relocations.h2")
        relocate ("org.mariadb", "net.impactdev.impactor.relocations.mariadb")
        relocate ("com.typesafe.config", "net.impactdev.impactor.relocations.typesafe.config")
        relocate ("cloud", "net.impactdev.impactor.relocations.cloud")
    }

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to rootProject.version)
        }
    }
}