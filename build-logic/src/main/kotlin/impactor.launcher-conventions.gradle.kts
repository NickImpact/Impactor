import net.fabricmc.loom.task.RemapJarTask

plugins {
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
        archiveVersion.set(writeVersion())
    }

}

tasks.withType<PublishToMavenRepository> {
    dependsOn(tasks["remapProductionJar"])
}

tasks.withType<GenerateModuleMetadata> {
    dependsOn(tasks["remapProductionJar"])
}

fun writeVersion(): String
{
    val plugin = rootProject.property("plugin")
    val minecraft = rootProject.property("minecraft")
    val snapshot = rootProject.property("snapshot") == "true"

    var version = "$plugin+$minecraft"
    if(snapshot) {
        version = "$version-SNAPSHOT"
    }

    return version
}