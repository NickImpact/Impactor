import extensions.writeVersion
import tasks.GenerateChangelog
import java.nio.file.Files

plugins {
    `java-library`
}

tasks {
    val collect by registering(Copy::class) {
        val filters = mapOf(
            ":launchers:fabric" to "remapProductionJar",
            ":launchers:forge" to "remapProductionJar",
        )

        val tasks = subprojects.filter { filters.containsKey(it.path) }.map { it.tasks.named(filters.getValue(it.path)) }
        dependsOn(tasks)
        from(tasks)
        into(buildDir.resolve("deploy"))
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

    build {
        dependsOn(writeChangelog)
    }

    assemble {
        dependsOn(collect)
    }
}