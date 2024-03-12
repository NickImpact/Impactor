import extensions.isRelease
import extensions.writeVersion
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.configurationcache.extensions.capitalized

plugins {
    id("impactor.loom-conventions")

    id("com.modrinth.minotaur")
    id("com.github.johnrengelman.shadow")
}

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    listOf(
        "net.kyori:examination-api:1.3.0",
        "net.kyori:examination-string:1.3.0",
        "net.kyori:adventure-api:4.14.0",
        "net.kyori:adventure-key:4.14.0",
        "net.kyori:adventure-nbt:4.14.0",
        "net.kyori:adventure-text-serializer-plain:4.14.0",
        "net.kyori:adventure-text-serializer-legacy:4.14.0",
        "net.kyori:adventure-text-serializer-gson:4.14.0",
        "net.kyori:adventure-text-serializer-json:4.14.0",
        "net.kyori:adventure-text-minimessage:4.14.0",
        "net.kyori:adventure-text-logger-slf4j:4.14.0",
        "net.kyori:event-api:5.0.0-SNAPSHOT",
    ).forEach { include(it) }
}

tasks {
    val remapProductionJar by registering(RemapJarTask::class) {
        listOf(shadowJar, remapJar).forEach {
            dependsOn(it)
            mustRunAfter(it)
        }

        inputFile.set(shadowJar.flatMap { it.archiveFile })

        archiveBaseName.set("Impactor-${project.name.capitalize()}")
        archiveVersion.set(writeVersion(true))
    }
    val minecraft = rootProject.property("minecraft")

    shadowJar {
        archiveBaseName.set("Impactor-${project.name}")
        archiveClassifier.set("dev-shadow")

        dependencies {
            include(project(":api:core"))
            include(project(":api:config"))
            include(project(":api:economy"))
            include(project(":api:items"))
            include(project(":api:mail"))
            include(project(":api:players"))
            include(project(":api:plugins"))
            include(project(":api:scoreboard"))
            include(project(":api:storage"))
            include(project(":api:text"))
            include(project(":api:translations"))
            include(project(":api:ui"))
            include(project(":impactor"))
            include(project(":minecraft:api"))
            include(project(":minecraft:impl"))

            include(dependency("net.impactdev:json:.*"))
            include(dependency("net.impactdev.impactor.api:commands:.*"))

            listOf(
                "com.zaxxer:HikariCP:.*",
                "com.h2database:h2:.*",
                "mysql:mysql-connector-java:.*",
                "org.mariadb.jdbc:mariadb-java-client:.*",
                "org.mongodb:mongo-java-driver:.*",
                "com.github.ben-manes.caffeine:caffeine:.*",
                "io.github.classgraph:classgraph:.*",
                "com.squareup.okhttp3:okhttp:.*",
                "com.squareup.okio:okio-jvm:.*",
                "com.typesafe:config:.*",
                "org.spongepowered:configurate-core:.*",
                "org.spongepowered:configurate-gson:.*",
                "org.spongepowered:configurate-hocon:.*",
                "org.spongepowered:configurate-yaml:.*",
                "org.spongepowered:math:.*",
                "org.jetbrains.kotlin:kotlin-stdlib:1.7.10"
            ).forEach { include(dependency(it)) }
        }

        val prefix = "net.impactdev.impactor.relocations"
        listOf(
            "com.typesafe.config",
            "com.zaxxer.hikari",
            "org.h2",
            "com.github.benmanes.caffeine",
            "io.github.classgraph",
            "com.mysql",
            "org.mariadb.jdbc",
            "com.mongodb",
            "org.bson",
            "nonapi.io.github.classgraph",
            "okhttp3",
            "okio",
            "org.spongepowered.configurate",
            "org.spongepowered.math",
            "kotlin"
        ).forEach { relocate(it, "$prefix.$it") }
    }

    remapJar {
        archiveBaseName.set("Impactor-${project.name.capitalize()}")
        archiveVersion.set("${minecraft}-${rootProject.version}")
    }
}

tasks.withType<PublishToMavenRepository> {
    dependsOn(tasks["remapProductionJar"])
}

tasks.withType<GenerateModuleMetadata> {
    dependsOn(tasks["remapProductionJar"])
}

modrinth {
    token.set(System.getenv("MODRINTH_GRADLE_TOKEN"))
    projectId.set("Impactor")
    versionNumber.set("${writeVersion(true)}-${project.name.capitalized()}")
    versionName.set("Impactor ${writeVersion(true)}")

    versionType.set(if(!isRelease()) "beta" else "release")
    uploadFile.set(tasks["remapProductionJar"])

    gameVersions.set(listOf(rootProject.property("minecraft").toString()))

    // https://github.com/modrinth/minotaur
    // TODO - Project Body Sync
    changelog.set(readChangelog())
    debugMode.set(true)
}

fun readChangelog(): String {
    val plugin = rootProject.property("plugin")
    val contents = rootProject.layout.buildDirectory
        .asFile
        .get()
        .resolve("deploy")
        .resolve("$plugin.md")

    if(!contents.exists()) {
        return "No changelog notes available..."
    }

    return contents.readLines().joinToString(separator = "\n")
}