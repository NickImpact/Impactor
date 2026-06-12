plugins {
    id("impactor.base-conventions")
    id("dev.architectury.loom-no-remap")
    id("architectury-plugin")
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
}
