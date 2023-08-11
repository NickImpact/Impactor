plugins {
    id("impactor.base-conventions")
    id("impactor.loom-conventions")
    id("impactor.publishing-conventions")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    api(project(":api:scoreboard"))
    api(project(":impactor"))
    api(project(":minecraft:api"))

    implementation("org.spongepowered:mixin:0.8.5")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    testImplementation("net.kyori:adventure-text-serializer-ansi:4.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor.minecraft"
            artifactId = "impl"

            val plugin = rootProject.property("plugin").toString()
            val minecraft = rootProject.property("minecraft").toString()
            val snapshot = rootProject.property("snapshot") == "true"

            version = "${plugin}+${minecraft}"
            if(snapshot) {
                version += "-SNAPSHOT"
            }
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}