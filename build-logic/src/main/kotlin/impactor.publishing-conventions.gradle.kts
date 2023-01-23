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

            val root = project.property("publication-root")!!.toString()
            groupId = "net.impactdev.impactor.$root"
            artifactId = project.name
            version = "${rootProject.version}"
        }
    }
}