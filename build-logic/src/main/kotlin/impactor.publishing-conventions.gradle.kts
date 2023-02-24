plugins {
    java
    `java-library`
    id("maven-publish")
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven("https://maven.impactdev.net/repository/development/") {
            name = "ImpactDev-Public"
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PW")
            }
        }
    }

    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor${project.findProperty("maven.root")?.let { ".$it" } ?: ""}"
            artifactId = project.findProperty("maven.artifactID")?.toString() ?: project.name
            version = "${rootProject.version}"
        }
    }
}