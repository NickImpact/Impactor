package tasks

import com.google.gson.GsonBuilder
import extensions.moduleVersion
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths

open class GenerateApiMetadata : DefaultTask() {

    @TaskAction
    fun run() {
        val gson = GsonBuilder().setPrettyPrinting().create()

        val target = project.file(Paths.get(project.projectDir.path)
            .resolve("src")
            .resolve("main")
            .resolve("resources")
            .resolve("impactor")
            .resolve(project.name)
            .resolve("metadata.json")
        )

        target.parentFile.mkdirs()
        val metadata = ProjectMetadata(project.moduleVersion())
        target.writeText(gson.toJson(metadata))
    }

}

internal data class ProjectMetadata(val version: String)