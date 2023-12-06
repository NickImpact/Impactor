import extensions.isRelease
import extensions.writeVersion
import gradle.kotlin.dsl.accessors._99454b32dbd9d870c3769e463ec2442a.include
import net.fabricmc.loom.task.RemapJarTask
import java.nio.file.Files

plugins {
    id("impactor.loom-conventions")
    id("com.modrinth.minotaur")
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
        archiveVersion.set(writeVersion())
    }
    val minecraft = rootProject.property("minecraft")

    shadowJar {
        dependencies {
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
    versionNumber.set(writeVersion())
    versionName.set("Impactor ${writeVersion()}")

    versionType.set(if(!isRelease()) "beta" else "release")
    uploadFile.set(tasks.remapJar)

    gameVersions.set(listOf(rootProject.property("minecraft").toString()))

    // https://github.com/modrinth/minotaur
    // TODO - Project Body Sync
    changelog.set(readChangelog())
}

fun readChangelog(): String {
    val plugin = rootProject.property("plugin")
    val contents = rootProject.buildDir.toPath()
        .resolve("deploy")
        .resolve("$plugin.md")

    if(!Files.exists(contents)) {
        return "No changelog notes available..."
    }

    return contents.toFile().readLines().joinToString(separator = "\n")
}