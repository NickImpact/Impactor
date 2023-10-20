import java.io.ByteArrayOutputStream

plugins {
    `java-library`
    id("org.cadixdev.licenser")
    id("net.kyori.blossom")
    kotlin("jvm")
}

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

version = rootProject.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        dependsOn(updateLicenses)
        finalizedBy(test)
    }

    jar {
        if(project.parent?.name.equals("api")) {
            archiveBaseName.set("Impactor-API-${project.name.substring(0, 1).toUpperCase()}${project.name.substring(1)}")
        } else {
            archiveBaseName.set("Impactor-${project.name.substring(0, 1).toUpperCase()}${project.name.substring(1)}")
        }
        archiveClassifier.set("dev-slim")
    }
}

license {
    header(rootProject.file("HEADER.txt"))
    properties {
        this.set("name", "Impactor")
        this.set("url", "https://github.com/NickImpact/Impactor/")
        this.set("year", 2022)
    }
}

blossom {
    replaceToken("@version@", project.version)
    replaceToken("@githash@", getLatestGitCommitHash(project))
}

fun getLatestGitCommitHash(project: Project) : String {
    return try {
        val byteOut = ByteArrayOutputStream()
        project.exec {
            this.commandLine = "git rev-parse --short HEAD".split(" ")
            this.standardOutput = byteOut
        }

        byteOut.toString("UTF-8").trim()
    } catch (ex: Exception) {
        "Unknown"
    }
}