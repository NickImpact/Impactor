plugins {
    `java-library`
    id("org.cadixdev.licenser")
    id("net.kyori.blossom")
}

repositories {
    mavenCentral()
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://libraries.minecraft.net")
}

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

license {
    header(rootProject.file("HEADER.txt"))
    properties {
        this.set("name", "Impactor")
        this.set("url", "https://github.com/NickImpact/Impactor/")
        this.set("year", 2022)
    }
}