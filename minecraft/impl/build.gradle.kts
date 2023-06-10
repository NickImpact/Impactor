plugins {
    id("impactor.base-conventions")
    id("impactor.publishing-conventions")
    id("org.spongepowered.gradle.vanilla")
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
    api(project(":minecraft:api"))
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor.minecraft"
            artifactId = "impl"

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