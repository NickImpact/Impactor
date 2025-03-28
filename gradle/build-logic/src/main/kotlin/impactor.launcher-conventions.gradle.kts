import extensions.isRelease
import extensions.writeVersion
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.configurationcache.extensions.capitalized

plugins {
    id("com.modrinth.minotaur")
    id("impactor.loom-conventions")
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