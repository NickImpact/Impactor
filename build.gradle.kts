buildscript {
    repositories {
        mavenCentral()
        maven("https://repository.jboss.org/nexus/content/groups/public/")
        maven("https://repo-new.spongepowered.org/repository/maven-snapshots")
    }
    dependencies {
        classpath("com.google.guava:guava:27.1-jre")
        classpath("org.hibernate.build.gradle:gradle-maven-publish-auth:2.0.1")
    }
}

plugins {
    base
    id("java")
    id("java-library")
    id("org.cadixdev.licenser") version "0.6.1"
    id("net.kyori.blossom") version "1.3.0" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "net.impactdev.impactor"
version = "5.0.0-SNAPSHOT"

tasks {
    val collect by registering(Copy::class) {
        val tasks = subprojects.filter { it.path != ":api" && it.path != ":common" }.map { it.tasks.named("remapJar") }
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
            languageVersion.set(JavaLanguageVersion.of(8))
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
    if(path != ":api" && path != ":common" && path != ":launcher") {
        apply(plugin = "com.github.johnrengelman.shadow")
    }
}