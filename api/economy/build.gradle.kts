plugins {
    id("impactor.base-conventions")
}

sourceSets {
    main {
        java.srcDirs(
            "src/builtin/java",
            "src/launcher/java"
        )

        resources.srcDirs(
            "src/builtin/resources",
            "src/launcher/resources"
        )
    }
}

dependencies {
    // Impactor
    implementation(projects.loader)
    api(projects.api.core)
    api(projects.api.config)
    api(projects.api.events)
    api(projects.api.networking)
    api(projects.api.storage)
    api(projects.api.schedulers)

    // Events
    compileOnly(libs.sponge.events.annotations)
    annotationProcessor(libs.sponge.events.processor)

    // Adventure
    api(libs.adventure.api)
    api(libs.adventure.key)
    api(libs.adventure.minimessage)

    // Google
    implementation(libs.google.guice)
    implementation(libs.google.auto.service)
    annotationProcessor(libs.google.auto.service)

    // Testing
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.log4j.api)
    testImplementation(libs.log4j.core)
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