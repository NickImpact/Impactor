plugins {
    id("impactor.base-conventions")
    id("impactor.publishing-conventions")
    id("org.spongepowered.gradle.vanilla") version("0.2.1-SNAPSHOT")
}

minecraft {
    version("1.16.5")
}

dependencies {
    api(project(":impactor"))
    api(project(":api:items"))
    api(project(":api:players"))
    api(project(":api:ui"))
}
