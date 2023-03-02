plugins {
    id("impactor.base-conventions")
    id("impactor.shadow-conventions")

    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    minecraft = rootProject.property("minecraft").toString()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.impactor.${project.name}.refmap.json")
    }
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())

//    listOf(
//        "net.kyori:examination-api:1.3.0",
//        "net.kyori:examination-string:1.3.0",
//        "net.kyori:adventure-api:4.11.0",
//        "net.kyori:adventure-key:4.11.0",
//        "net.kyori:adventure-nbt:4.11.0",
//        "net.kyori:adventure-text-serializer-plain:4.11.0",
//        "net.kyori:adventure-text-serializer-legacy:4.11.0",
//        "net.kyori:adventure-text-serializer-gson:4.11.0",
//        "net.kyori:adventure-text-minimessage:4.11.0",
//        "net.kyori:adventure-text-logger-slf4j:4.11.0",
//        "net.kyori:event-api:5.0.0-SNAPSHOT",
//        "com.typesafe:config:1.4.1",
//        "org.spongepowered:configurate-core:4.1.2",
//        "org.spongepowered:configurate-gson:4.1.2",
//        "org.spongepowered:configurate-hocon:4.1.2",
//        "org.spongepowered:configurate-yaml:4.1.2",
//        "com.zaxxer:HikariCP:4.0.3",
//        "com.h2database:h2:2.1.214",
//        "mysql:mysql-connector-java:8.0.28",
//        "org.mariadb.jdbc:mariadb-java-client:2.7.2",
//        "org.mongodb:mongo-java-driver:3.12.2",
//        "org.spongepowered:math:2.0.1",
//        "io.leangen.geantyref:geantyref:1.3.11",
//        "com.github.ben-manes.caffeine:caffeine:2.9.3",
//        "io.github.classgraph:classgraph:4.8.149",
//        "ca.landonjw.gooeylibs:forge:3.0.0-1.18.2-SNAPSHOT"
//    ).forEach { include(it) }
}

tasks {
    val minecraft = rootProject.property("minecraft")

    shadowJar {
        dependencies {
            include(dependency("cloud.commandframework:cloud-core:.*"))
            include(dependency("cloud.commandframework:cloud-annotations:.*"))
            include(dependency("cloud.commandframework:cloud-brigadier:.*"))
            include(dependency("cloud.commandframework:cloud-services:.*"))
        }
    }

    remapJar {
        archiveBaseName.set("Impactor-${project.name.capitalize()}")
        archiveVersion.set("${minecraft}-${rootProject.version}")
    }
}