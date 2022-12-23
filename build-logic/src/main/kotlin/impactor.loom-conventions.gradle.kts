plugins {
    id("impactor.base-conventions")
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
}

architectury {
    minecraft = rootProject.property("minecraft").toString()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.impactor.${project.name}.refmap.json")
    }
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
}

tasks {
    jar {
        archiveBaseName.set("Impactor-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        archiveBaseName.set("Impactor-${project.name}")
        archiveClassifier.set("dev-shadow")

        dependencies {
            include(project(":api:core"))
            include(project(":api:config"))
            include(project(":api:commands"))
            include(project(":api:economy"))
            include(project(":api:items"))
            include(project(":api:players"))
            include(project(":api:plugins"))
            include(project(":api:storage"))
            include(project(":api:text"))
            include(project(":api:ui"))
            include(project(":impactor"))
            include(project(":game"))

            include(dependency("net.impactdev:json:.*"))
        }
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })

        val minecraft = rootProject.property("minecraft")
        val target = rootProject.property(project.name.toLowerCase())

        archiveBaseName.set("Impactor-${project.property("platform.name") ?: project.name}")
        archiveClassifier.set("")
        archiveVersion.set("$minecraft-$target-${rootProject.version}")
    }
}