import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("impactor.loom-conventions")
}

tasks {
    val minecraft = rootProject.property("minecraft")

    val remapProductionJar by registering(RemapJarTask::class) {
        dependsOn(shadowJar)
        mustRunAfter(shadowJar)
        mustRunAfter(remapJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })

        archiveBaseName.set("Impactor-${project.name.capitalize()}")
        archiveVersion.set("${minecraft}-${rootProject.version}")
    }
}