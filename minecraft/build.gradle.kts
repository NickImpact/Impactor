plugins {
    id("impactor.base-conventions")
    id("impactor.publishing-conventions")
    id("org.spongepowered.gradle.vanilla") version("0.2.1-SNAPSHOT")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

minecraft {
    version("${rootProject.property("minecraft")}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    api(project(":impactor"))
    api(project(":api:items"))
    api(project(":api:ui"))
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor.integration"
            artifactId = "minecraft"

            val plugin = rootProject.property("plugin").toString()
            val minecraft = rootProject.property("minecraft").toString()
            val snapshot = rootProject.property("snapshot") == "true"

            version = "${plugin}+${minecraft}"
            if(snapshot) {
                version += "-SNAPSHOT"
            }
        }
    }
}
