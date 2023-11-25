import extensions.writeVersion

plugins {
    id("impactor.base-conventions")
    id("impactor.publishing-conventions")
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor${project.findProperty("maven.root")?.let { ".$it" } ?: ""}"
            artifactId = project.findProperty("maven.artifactID")?.toString() ?: project.name
            version = writeVersion()
        }
    }
}