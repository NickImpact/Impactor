plugins {
    id("impactor.base-conventions")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

minecraft {
    version("${rootProject.property("minecraft")}")
}

dependencies {
    api(project(":impactor"))
    api(project(":api:items"))
    api(project(":api:ui"))
}
