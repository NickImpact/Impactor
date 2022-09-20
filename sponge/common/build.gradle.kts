import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("org.spongepowered.gradle.plugin") version "2.0.1"
}

sponge {
    apiVersion("8.0.0")
    license("MIT")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("impactor") {
        displayName("Impactor")
        version(rootProject.version.toString())
        entrypoint("net.impactdev.impactor.sponge.SpongeImpactorBootstrap")
        description("Multi-platform utility API")
        links {
            source("https://github.com/NickImpact/Impactor")
            issues("https://github.com/NickImpact/Impactor/issues")
        }
        contributor("NickImpact") {
            description("Lead Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-snapshots") {
        name = "Sponge Snapshots"
    }
    maven("https://repo.spongepowered.org/repository/maven-releases") {
        name = "Sponge Releases"
    }
}

dependencies {
    api(project(":api"))
    api(project(":common"))

    implementation("org.spongepowered:spongeapi:8.1.0")
}