import extensions.writeVersion

plugins {
    id("impactor.base-conventions")
    id("impactor.loom-conventions")
    id("impactor.publishing-conventions")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    api(project(":api:items"))
    api(project(":api:ui"))
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor.minecraft"
            artifactId = "api"
            version = writeVersion(true)
        }
    }
}