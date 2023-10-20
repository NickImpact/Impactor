import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("impactor.loom-conventions")
    id("com.modrinth.minotaur")
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

    val snapshot = rootProject.property("snapshot") == "true"
    versionType.set(if(snapshot) "beta" else "release")
    uploadFile.set(tasks.remapJar)

    gameVersions.set(listOf(rootProject.property("minecraft").toString()))

    // TODO - Changelog integration
}