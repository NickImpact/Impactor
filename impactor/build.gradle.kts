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

    // Cloud Command Framework
    api("cloud.commandframework:cloud-core:1.7.1")
    api("cloud.commandframework:cloud-annotations:1.7.1")
    api("cloud.commandframework:cloud-brigadier:1.7.1")
    implementation("com.mojang:brigadier:1.0.18")

    // Databases
    api("com.zaxxer:HikariCP:4.0.3")
    api("com.h2database:h2:2.1.214")
    api("mysql:mysql-connector-java:8.0.32")
    api("org.mariadb.jdbc:mariadb-java-client:3.1.2")
    api("org.mongodb:mongo-java-driver:3.12.2")

    api("com.github.ben-manes.caffeine:caffeine:2.9.3")
    implementation("io.github.classgraph:classgraph:4.8.157")
    implementation("net.luckperms:api:5.4")
    implementation("me.lucko:spark-api:0.1-SNAPSHOT")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.mojang:brigadier:1.0.18")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.mockito:mockito-core:4.7.0")
}

tasks.withType(Test::class) {
    useJUnitPlatform()

    // Allow JUnit to find our TestInitializer and invoke its
    // before all callback for all tests
    jvmArgs("-Djunit.jupiter.extensions.autodetection.enabled=true")
}