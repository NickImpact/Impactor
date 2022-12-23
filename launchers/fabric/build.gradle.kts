plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
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

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric")}")

    implementation(project(":game"))

    modImplementation("ca.landonjw.gooeylibs:api:3.0.0-SNAPSHOT")
    modImplementation("ca.landonjw.gooeylibs:fabric:3.0.0-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}