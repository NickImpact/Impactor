import tasks.GenerateApiMetadata

plugins {
    `java-library`
}

tasks {
    val metadata = tasks.create("generateMetadata", GenerateApiMetadata::class)
    processResources {
        dependsOn(metadata)
    }
}