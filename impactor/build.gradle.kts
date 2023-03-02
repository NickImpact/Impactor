plugins {
    id("impactor.base-conventions")
    id("impactor.publishing-conventions")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    api(project(":api:core"))
    api(project(":api:config"))
    api(project(":api:commands"))
    api(project(":api:economy"))
    api(project(":api:plugins"))
    api(project(":api:storage"))
    api(project(":api:text"))

    api("com.github.ben-manes.caffeine:caffeine:2.9.3")
    implementation("io.github.classgraph:classgraph:4.8.149")
    implementation("net.luckperms:api:5.4")
    implementation("me.lucko:spark-api:0.1-SNAPSHOT")
    implementation("commons-io:commons-io:2.5")
    implementation("org.apache.commons:commons-lang3:3.5")
    implementation("com.mojang:brigadier:1.0.18")

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