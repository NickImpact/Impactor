import extensions.writeVersion

plugins {
    id("impactor.base-conventions")
    id("impactor.publishing-conventions")
    id("org.spongepowered.gradle.vanilla")
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

minecraft {
    version("${rootProject.property("minecraft")}")
}

dependencies {
    api(project(":api:scoreboard"))
    api(project(":impactor"))
    api(project(":minecraft:api"))

    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("org.spongepowered:mixin:0.8.5")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")

    testImplementation("net.kyori:adventure-text-serializer-ansi:4.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("net.kyori:adventure-text-logger-slf4j:4.14.0")
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor.minecraft"
            artifactId = "impl"
            version = writeVersion()
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()

    // Allow JUnit to find our TestInitializer and invoke its
    // before all callback for all tests
    jvmArgs("-Djunit.jupiter.extensions.autodetection.enabled=true")
}