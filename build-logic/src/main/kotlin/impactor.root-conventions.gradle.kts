plugins {
    `java-library`
}

tasks {
    val collect by registering(Copy::class) {
        val filters = mapOf(
            ":launchers:fabric" to "remapJar",
            ":launchers:forge" to "remapProductionJar",
        )

        val tasks = subprojects.filter { filters.containsKey(it.path) }.map { it.tasks.named(filters.getValue(it.path)) }
        dependsOn(tasks)
        from(tasks)
        into(buildDir.resolve("deploy"))
    }

    assemble {
        dependsOn(collect)
    }
}