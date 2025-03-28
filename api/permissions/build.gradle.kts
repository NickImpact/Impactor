plugins {
    id("impactor.base-conventions")
}

dependencies {
    implementation(projects.api.core)
    implementation(projects.api.platform)

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
        options.compilerArgs.addAll(listOf(
            "-AeventGenInclusiveFolders=net.impactdev.impactor.api.economy.events",
            "-AeventGenFactory=net.impactdev.impactor.api.economy.events.EconomyEventFactory",
            "-AeventGenDebug=true"
        ))
    }
}
sourceSets {
    main {
        java.srcDir(generatedEventSourcesDir)
    }
}