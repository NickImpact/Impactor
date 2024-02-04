package tasks

import extensions.getLatestTag
import extensions.getPreviousTag
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import placeholders.Placeholder
import placeholders.PlaceholderRegistry
import java.io.File
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolute

open class GenerateChangelog : DefaultTask() {

    @get:Internal
    var result: String = "No changelog notes available..."

    private fun fetch(): File {
        val version = this.project.rootProject.property("plugin")
        val target = this.project.rootDir.toPath().resolve("changelogs").resolve("${version}.md").absolute()

        if(Files.exists(target)) {
            return target.toFile()
        }

        throw IllegalStateException()
    }

    private fun compare(): Placeholder {
        val previous = this.project.getPreviousTag()
        val current = this.project.getLatestTag()

        return Placeholder { "[$previous...$current](https://github.com/NickImpact/Impactor/compare/$previous...$current)" }
    }

    private fun generate(): String {
        val target = this.fetch()
        PlaceholderRegistry.register("tags:history", this.compare())

        val base = Paths.get("changelogs").resolve("base.md").toFile().readLines(Charsets.UTF_8).joinToString(separator = "\n")
        val input = target.readLines(Charsets.UTF_8).joinToString(separator = "\n")
        return PlaceholderRegistry.parse(this.project, "$base\n\n$input")
    }

    @TaskAction
    fun run() {
        try {
            this.result = this.generate()
        } catch (_: Exception) {}
    }
}