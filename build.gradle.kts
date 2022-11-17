buildscript {
    repositories {
        mavenCentral()
        maven("https://repository.jboss.org/nexus/content/groups/public/")
        maven("https://repo-new.spongepowered.org/repository/maven-snapshots")
    }
    dependencies {
        classpath("com.google.guava:guava:27.1-jre")
    }
}

plugins {
    base
    id("java")
    id("java-library")
    id("org.cadixdev.licenser") version "0.6.1"
    id("net.kyori.blossom") version "1.3.0" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("dev.architectury.loom") version "0.12.0-SNAPSHOT" apply false
}

group = "net.impactdev.impactor"
version = "5.0.0-SNAPSHOT"

tasks {
    val collect by registering(Copy::class) {
        val filters = mapOf(
            ":fabric" to "remapJar",
            ":forge" to "remapJar",
            ":bukkit" to "shadowJar"
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

tasks.withType<Test> {
    useJUnitPlatform()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "net.kyori.blossom")
    apply(plugin = "org.cadixdev.licenser")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            dependsOn(updateLicenses)
            finalizedBy(test)
        }
    }

    repositories {
        mavenCentral()
        maven("https://maven.impactdev.net/repository/development/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    }

    license {
        header(file("../HEADER.txt"))
        properties {
            this.set("name", "Impactor")
            this.set("url", "https://github.com/NickImpact/Impactor/")
            this.set("year", 2022)
        }
    }
}

subprojects {
    val filters = listOf(":api", ":common", ":sponge")
    if(!filters.contains(path)) {
        apply(plugin = "com.github.johnrengelman.shadow")
    }
}