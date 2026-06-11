plugins {
    base
    id("impactor.root-conventions")
    id("architectury-plugin") version "3.5-SNAPSHOT" apply false
    id("dev.architectury.loom-no-remap") version "1.17-SNAPSHOT" apply false
    id("org.spongepowered.gradle.vanilla") version "0.3.2-SNAPSHOT" apply false
}

group = "net.impactdev.impactor"
version = properties["plugin"]!!

val isSnapshot = project.property("snapshot")?.equals("true") ?: false
if (isSnapshot) {
    version = "$version-SNAPSHOT"
}