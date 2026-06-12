import extensions.isRelease
import extensions.writeVersion

plugins {
    id("impactor.loom-conventions")

    id("com.modrinth.minotaur")
    id("com.gradleup.shadow")
}

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    listOf(
        "net.kyori:examination-api:1.3.0",
        "net.kyori:examination-string:1.3.0",
        "net.kyori:adventure-api:4.26.1",
        "net.kyori:adventure-key:4.26.1",
        "net.kyori:adventure-nbt:4.26.1",
        "net.kyori:adventure-text-serializer-plain:4.26.1",
        "net.kyori:adventure-text-serializer-legacy:4.26.1",
        "net.kyori:adventure-text-serializer-gson:4.26.1",
        "net.kyori:adventure-text-serializer-json:4.26.1",
        "net.kyori:adventure-text-minimessage:4.26.1",
        "net.kyori:adventure-text-logger-slf4j:4.26.1",
        "com.github.KyoriPowered.event:event-api:master-SNAPSHOT",
    ).forEach { include(it) }
}

tasks {
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
            include(dependency("net.impactdev.impactor.commands:common:.*"))

            listOf(
                "com.zaxxer:HikariCP:.*",
                "com.h2database:h2:.*",
                "com.mysql:mysql-connector-j:.*",
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
                "org.jetbrains.kotlin:kotlin-stdlib:.*",
                "redis.clients:jedis:.*",
                "com.rockbb.jedis:jedis-tool-kit:.*",
                "org.apache.commons:commons-pool2:.*",
                "org.apache.maven:maven-artifact:.*",
                "org.json:json:.*"
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
            "kotlin",
            "redis.clients",
            "com.rockbb.jedis.toolkit",
            "org.apache.commons.pool2",
            "org.json",
            "org.apache.maven"
        ).forEach { relocate(it, "$prefix.$it") }
    }
}

tasks.withType<PublishToMavenRepository> {
    dependsOn(tasks["shadowJar"])
}

tasks.withType<GenerateModuleMetadata> {
    dependsOn(tasks["shadowJar"])
}

tasks.modrinth {
    dependsOn(tasks.modrinthSyncBody)
}

modrinth {
    token.set(System.getenv("MODRINTH_GRADLE_TOKEN"))
    projectId.set("Impactor")
    versionNumber.set("${writeVersion(true)}-${project.name}")
    versionName.set("Impactor ${writeVersion(true)}")

    versionType.set(if(!isRelease()) "beta" else "release")
    uploadFile.set(tasks["shadowJar"])

    gameVersions.set(listOf(rootProject.property("minecraft").toString()))

    syncBodyFrom.set(rootProject.file("README.md").readText(Charsets.UTF_8))
    changelog.set(readChangelog())
    debugMode.set(!project.isRelease())
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