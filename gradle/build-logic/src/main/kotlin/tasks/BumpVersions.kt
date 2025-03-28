package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.util.Scanner

open class BumpVersions : DefaultTask() {

    @TaskAction
    fun run() {
        val scanner = Scanner(System.`in`)

        val toUpdate = mutableMapOf<Project, UpdateType>()
        while(true) {
            println("Enter module name to update, or done to continue")

            var input = scanner.nextLine()
            if(input == "done") {
                break
            }

            // Bump all versions. To be used when buildscript changes are made
            if(input == "allPatch") {
                project.childProjects.values.forEach {
                    if(it.name == "deprecated") {
                        return
                    }

                    toUpdate[it] = UpdateType.PATCH
                }

                break
            }

            val target = project.childProjects["api"]?.childProjects?.get(input)
            if(target == null) {
                println("Could not find project with name: $input")
                continue
            }

            while(true) {
                println("Bump version for ${target.name}")
                println("1) Bump Major")
                println("2) Bump Minor")
                println("3) Bump Patch")

                input = scanner.nextLine()

                if(input !in arrayOf("1", "2", "3")) {
                    println("Invalid input")
                    continue
                }

                toUpdate[target] = UpdateType.values()[input.toInt() - 1]
                break
            }

            while(true) {
                val temp = mutableMapOf<Project, UpdateType>()
                toUpdate.keys.forEach { p ->
                    println("Working project: $p")

                    project.childProjects["api"]?.childProjects!!.values.forEach { cp ->
                        if(cp.name == "deprecated") {
                            return
                        }

                        val config = cp.configurations.findByName("api")
                        config!!.allDependencies.forEach { dep ->
                            if(dep.name == p.name) {
                                if(!toUpdate.containsKey(cp)) {
                                    println("Bumping patch of ${cp.name} as it depends on ${p.name}")
                                    temp[cp] = UpdateType.PATCH
                                }
                            }
                        }
                    }
                }

                if(temp.isEmpty()) {
                    break
                }

                toUpdate.putAll(temp)
            }

            val file = project.file("gradle.properties")
            val properties = project.properties
            var text = file.readText(Charsets.UTF_8)

            toUpdate.forEach { (p, i) ->
                val version = properties[p.name]?.toString()
                    ?: throw NullPointerException("Could not find version for ${p.name}")

                val split = version.split(Regex("\\.")).toMutableList()
                val index = when(i) {
                    UpdateType.MAJOR -> 0
                    UpdateType.MINOR -> 1
                    UpdateType.PATCH -> 2
                }

                println(split)
                split[index] = "${split[index].toInt() + 1}"
                for(j in (index + 1) until split.size) {
                    split[j] = "0"
                }

                val newVersion = split.joinToString(".")
                println("${p.name}: $version --> $newVersion")

                text = text.replace("${p.name} = $version", "${p.name} = $newVersion")
            }

            file.writeText(text, Charsets.UTF_8)
            break
        }
    }

}

enum class UpdateType {
    MAJOR,
    MINOR,
    PATCH
}