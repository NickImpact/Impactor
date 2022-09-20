dependencies {
    implementation(project(":sponge:common"))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        val minecraft = rootProject.property("minecraft")
        val sponge = rootProject.property("sponge")

        archiveBaseName.set("Impactor-SpongeVanilla")
        archiveClassifier.set("")
        archiveVersion.set("$minecraft-$sponge-${rootProject.version}")

        dependencies {
            include(project(":api"))
            include(project(":common"))
            include(project(":sponge:common"))
            include(dependency("net.kyori:event-api:.*"))
            include(dependency("net.impactdev:json:.*"))
            include(dependency("com.github.ben-manes.caffeine:caffeine:.*"))
        }

        relocate ("net.kyori.event", "net.impactdev.impactor.relocations.kyori.event")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
    }
}