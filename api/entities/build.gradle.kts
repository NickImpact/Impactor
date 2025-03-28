plugins {
    id("impactor.minecraft-conventions")
}

dependencies {
    implementation(projects.api.core)
    implementation(projects.api.commands)
    implementation(projects.api.permissions)

    implementation(libs.bundles.mixins)
    annotationProcessor(libs.bundles.mixins)

    implementation(libs.sponge.math)
    compileOnly(libs.sponge.events.annotations)
    annotationProcessor(libs.sponge.events.processor)
}

val generatedEventSourcesDir = project.file("src/main/generated")
tasks {
    register("printSourceDirs") {
        doLast {
            sourceSets.forEach { set ->
                println("Source Set: ${set.name}")
                println("Java Source Dirs: ${set.java.srcDirs}")
            }
        }
    }

    compileJava {
        options.generatedSourceOutputDirectory.set(generatedEventSourcesDir)
        options.compilerArgs.addAll(listOf(
            "-AeventGenInclusiveFolders=net/impactdev/impactor/api/entities/events",
            "-AeventGenFactory=net.impactdev.impactor.api.entities.events.EntityEventFactory",
            "-AeventGenDebug=true"
        ))
    }
}

sourceSets {
    main {
        java.srcDir(generatedEventSourcesDir)
        java.srcDir("src/mixins/java")
        resources.srcDir("src/mixins/resources")
    }
}