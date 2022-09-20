plugins {
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

minecraft {
    version("1.16.5")
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":api"))

    api("com.github.ben-manes.caffeine:caffeine:2.9.3")
    implementation("io.github.classgraph:classgraph:4.8.149")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.mockito:mockito-core:4.7.0")
}

tasks.withType(Test::class) {
    useJUnitPlatform()

    // Allow JUnit to find our TestInitializer and invoke its
    // before all callback for all tests
    jvmArgs("-Djunit.jupiter.extensions.autodetection.enabled=true")
}