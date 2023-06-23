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
    api(project(":api:economy"))
    api(project(":api:players"))
    api(project(":api:plugins"))
    api(project(":api:storage"))
    api(project(":api:text"))
    api(project(":api:translations"))

    api("net.impactdev.impactor.api:commands:5.0.0+1.19.2-SNAPSHOT") {
        exclude("net.impactdev.impactor.api", "core")
        exclude("net.impactdev.impactor.api", "items")
        exclude("net.impactdev.impactor.api", "players")
    }

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

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
//    implementation("com.squareup.okio:okio:3.3.0")

    testImplementation("net.kyori:adventure-text-serializer-ansi:4.14.0")
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

sourceSets {
    test {
        resources {
            srcDirs.add(File("src/main/resources"))
            srcDirs.add(File("src/test/resources"))
        }
    }
}

license {
    exclude("**/datasize/DataSize.java")
    exclude("**/datasize/DataSizeUtils.java")
    exclude("**/datasize/DataUnit.java")
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            groupId = "net.impactdev.impactor"
            artifactId = "common"

            val snapshot = rootProject.property("snapshot") == "true"

            version = rootProject.property("plugin").toString()
            if(snapshot) {
                version += "-SNAPSHOT"
            }
        }
    }
}