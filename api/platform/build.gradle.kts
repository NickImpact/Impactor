plugins {
    id("impactor.base-conventions")
}

dependencies {
    implementation(projects.loader)
    api(projects.api.core)
    api(projects.api.events)
    api(libs.adventure.api)

    // Events
    compileOnly(libs.sponge.events.annotations)
    annotationProcessor(libs.sponge.events.processor)

    // Performance
    compileOnly(libs.spark)
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
            "-AeventGenInclusiveFolders=net/impactdev/impactor/api/platform/events",
            "-AeventGenFactory=net.impactdev.impactor.api.platform.events.PlatformEventFactory",
            "-AeventGenDebug=true"
        ))
    }
}
sourceSets {
    main {
        java.srcDir(generatedEventSourcesDir)
    }
}