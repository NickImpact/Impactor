plugins {
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-releases") {
        name = "Sponge Releases"
    }
    maven("https://libraries.minecraft.net")
}

dependencies {
    // Impact Dev Modules
    api("net.impactdev:json:1.0.0")

    // Adventure
    api("net.kyori:adventure-api:4.11.0")
    api("net.kyori:adventure-nbt:4.11.0")
    api("net.kyori:adventure-text-serializer-legacy:4.11.0")
    api("net.kyori:adventure-text-serializer-gson:4.11.0")
    api("net.kyori:adventure-text-minimessage:4.11.0")

    // Kyori Events
    api("net.kyori:event-api:5.0.0-SNAPSHOT")

    // Event Generation
    compileOnlyApi("org.spongepowered:event-impl-gen-annotations:8.0.0-SNAPSHOT")

    // Configurate
    api("org.spongepowered:configurate-core:4.1.2")
    api("org.spongepowered:configurate-gson:4.1.2")
    api("org.spongepowered:configurate-hocon:4.1.2")
    api("org.spongepowered:configurate-yaml:4.1.2")

    // Google
    api("com.google.guava:guava:31.1-jre")
    api("com.google.code.gson:gson:2.9.1")

    // Misc
    api("io.leangen.geantyref:geantyref:1.3.13")
    api(group = "org.spongepowered", name = "math", version = "2.0.1")
    implementation("org.apache.logging.log4j:log4j-api:2.18.0")
    implementation(group = "com.zaxxer", name = "HikariCP", version = "4.0.3")
    compileOnly("com.mojang:brigadier:1.0.18")
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
        create<MavenPublication>("api") {
            from(components["java"])
            groupId = "net.impactdev.impactor"
            artifactId = "api"
            version = rootProject.version.toString()
        }
    }
}
