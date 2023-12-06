plugins {
    id("impactor.base-conventions")
    id("impactor.shadow-conventions")

    id("dev.architectury.loom")
    id("architectury-plugin")
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
