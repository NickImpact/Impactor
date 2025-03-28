import extensions.writeVersion

plugins {
    `java-library`
    kotlin("jvm")

    id("org.cadixdev.licenser")
    id("net.kyori.indra")
    id("net.kyori.indra.git")
}

version = writeVersion(true)

repositories {
    mavenCentral()
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://libraries.minecraft.net")
    maven("https://oss.sonatype.org/content/repositories/snapshots") {
        name = "Sonatype Snapshots"
    }
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots") {
        name = "Sonatype 01 Snapshots"
    }
}

val bundle = configurations.create("bundle")
bundle.isCanBeConsumed = false
bundle.isCanBeResolved = true

indra {
    javaVersions {
        minimumToolchain(21)
        target(21)
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        dependsOn(updateLicenses)
        finalizedBy(test)
    }

    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()

    // Allow JUnit to find our TestInitializer and invoke its
    // before all callback for all tests
    jvmArgs("-Djunit.jupiter.extensions.autodetection.enabled=true")
}

license {
    header(rootProject.file("HEADER.txt"))
    properties {
        this.set("name", "Impactor")
        this.set("url", "https://github.com/NickImpact/Impactor/")
        this.set("year", 2024)
    }
}

fun getSubprojectVersion(project: Project) {
    val version = project.properties["${project.name}-version"]

    if(version != null) {
        throw NullPointerException("Version not found for ${project.name}")
    }
}