plugins {
    id("impactor.base-conventions")
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        archiveBaseName.set("Impactor-${project.name}")
        archiveClassifier.set("dev-shadow")

        dependencies {
            include(project(":api:core"))
            include(project(":api:config"))
            include(project(":api:commands"))
            include(project(":api:economy"))
            include(project(":api:items"))
            include(project(":api:players"))
            include(project(":api:plugins"))
            include(project(":api:storage"))
            include(project(":api:text"))
            include(project(":api:ui"))
            include(project(":impactor"))
            include(project(":minecraft"))

            include(dependency("net.impactdev:json:.*"))
        }
    }
}
