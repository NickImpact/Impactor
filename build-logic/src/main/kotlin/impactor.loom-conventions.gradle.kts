plugins {
    id("impactor.base-conventions")
    id("dev.architectury.loom")
    id("architectury-plugin")
}

loom {
    silentMojangMappingsLicense()

    val identifier: String = project.findProperty("identifier")?.toString() ?: project.name
    mixin.defaultRefmapName.set("mixins.impactor.$identifier.refmap.json")
    mixin.useLegacyMixinAp.set(false)
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
}
