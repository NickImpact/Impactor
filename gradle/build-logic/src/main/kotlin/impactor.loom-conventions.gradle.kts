import java.util.*

plugins {
    id("impactor.base-conventions")
    id("impactor.shadow-conventions")

    id("dev.architectury.loom")
    id("architectury-plugin")
}

loom {
    silentMojangMappingsLicense()
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
}

tasks {
    val minecraft = rootProject.property("minecraft")

    remapJar {
        archiveBaseName.set("Impactor-${project.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}")
        archiveVersion.set("${minecraft}-${rootProject.version}")

        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }
}