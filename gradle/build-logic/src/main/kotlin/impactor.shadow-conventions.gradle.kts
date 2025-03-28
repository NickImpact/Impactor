plugins {
    id("impactor.base-conventions")
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        archiveBaseName.set("Impactor-${project.name}")
        archiveClassifier.set("dev-shadow")

        configurations = listOf(project.configurations.getByName("bundle"))
    }
}
