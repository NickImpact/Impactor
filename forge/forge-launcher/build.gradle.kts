plugins {
    id("architectury-plugin")
    id("dev.architectury.loom")
}

architectury {
    platformSetupLoomIde()
    forge()
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge:forge:${rootProject.property("minecraft")}-${rootProject.property("forge")}")

    implementation(project(":launcher"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks {
    shadowJar {
        val minecraft = rootProject.property("minecraft")
        val forge = rootProject.property("forge")

        archiveBaseName.set("Impactor-Forge")
        archiveClassifier.set("")
        archiveVersion.set("$minecraft-$forge-${rootProject.version}")

        from(project(":forge").tasks.remapJar.map { it.outputs })
        dependencies {
            include(project(":api"))
            include(project(":launcher"))
            exclude("forge-client-extra.jar")
        }

        relocate ("io.leangen.geantyref", "net.impactdev.impactor.relocations.geantyref")
        relocate ("org.slf4j", "net.impactdev.impactor.relocations.slf4j")
        relocate ("net.kyori", "net.impactdev.impactor.relocations.kyori")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
        relocate ("org.reflections", "net.impactdev.impactor.relocations.reflections")
    }
}

license {
    header(file("../../HEADER.txt"))
}