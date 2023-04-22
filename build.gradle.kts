plugins {
    base
    id("impactor.root-conventions")
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("dev.architectury.loom") version "0.12.0-SNAPSHOT" apply false
}

group = "net.impactdev.impactor"
version = properties["plugin"]!!

val isSnapshot = project.property("snapshot")?.equals("true") ?: false
if (isSnapshot) {
    version = "$version-SNAPSHOT"
}