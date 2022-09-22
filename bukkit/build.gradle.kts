import java.nio.charset.StandardCharsets

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "Spigot MC Snapshots"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "Sonatype"
    }
}

dependencies {
    // Spigot API
    implementation("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
//    implementation("org.spigotmc:spigot:1.19.2:remapped-mojang")

    // Impactor Modules
    implementation(project(":api"))
    implementation(project(":common"))

    // Utility
//    implementation 'me.lucko:commodore:2.2'
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    shadowJar {
        dependencies {
            include(project(":api"))
            include(project(":common"))
            include(dependency("net.impactdev:json:.*"))
            include(dependency("net.kyori:.*:.*"))
            include(dependency("org.spongepowered:math:.*"))
            include(dependency("com.github.ben-manes.caffeine:caffeine:.*"))
            include(dependency("io.leangen.geantyref:geantyref:.*"))
        }

        relocate ("org.spongepowered", "net.impactdev.impactor.relocations.spongepowered")
        relocate ("io.leangen.geantyref", "net.impactdev.impactor.relocations.geantyref")
        relocate ("net.kyori", "net.impactdev.impactor.relocations.kyori")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
    }

    processResources {
        filteringCharset = StandardCharsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand(mapOf(Pair("version", rootProject.version.toString())))
        }
    }
}
