plugins {
    id("maven-publish")
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
        create<MavenPublication>("${project.name}") {
            from(components["java"])
            groupId = "net.impactdev.impactor.api"
            artifactId = "${project.name}"
            version = "${rootProject.version}"
        }
    }
}