plugins {
    base
    alias(libs.plugins.versions)

    id("impactor.root-conventions")
}

group = "net.impactdev.impactor"
version = properties["plugin"]!!

val isSnapshot = project.property("snapshot")?.equals("true") ?: false
if (isSnapshot) {
    version = "$version-SNAPSHOT"
}