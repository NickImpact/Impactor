import extensions.isRelease
import tasks.GenerateChangelog
import tasks.PublishToDiscord
import java.nio.file.Files

plugins {
    `java-library`
}

tasks {
    val readme = tasks.register("readme", Copy::class) {
        from("build-logic")
        into(project.layout.projectDirectory)
        include("README.template.md")
        rename { _ -> "README.md"}
        expand("version" to project.version)
        doNotTrackState("This is just a generation task")
    }

    val collect by registering(Copy::class) {
        val filters = mapOf(
            ":launchers:fabric" to "remapProductionJar",
            ":launchers:forge" to "remapProductionJar",
        )

        val tasks = subprojects.filter { filters.containsKey(it.path) }.map { it.tasks.named(filters.getValue(it.path)) }
        dependsOn(tasks)
        from(tasks)
        into(layout.buildDirectory.asFile.get().resolve("deploy"))
    }

    val changelog = tasks.register("changelog", GenerateChangelog::class)
    val writeChangelog by registering {
        dependsOn(changelog)
        doLast {
            val plugin = this.project.rootProject.property("plugin")
            val target = this.project.projectDir.toPath().resolve("$buildDir").resolve("deploy").resolve("$plugin.md")
            if(!Files.exists(target)) {
                Files.createDirectories(target.parent)
                Files.createFile(target)
            }

            Files.write(target, changelog.get().result.encodeToByteArray())
        }
    }

    val publishToDiscord = tasks.register("discord", PublishToDiscord::class)

    build {
        if(this.project.isRelease()) {
            dependsOn(writeChangelog)
        }
    }

    assemble {
        dependsOn(collect)
    }
}