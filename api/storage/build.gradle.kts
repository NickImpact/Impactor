plugins {
    id("impactor.api-conventions")
    id("impactor.base-conventions")
    id("impactor.shadow-conventions")
}

dependencies {
    // Impactor
    api(projects.api.core)
    implementation(projects.loader)

    // Google AutoService
    compileOnly(libs.google.auto.service)
    annotationProcessor(libs.google.auto.service)

    // Storage Drivers
    implementation(libs.hikari)
    sequenceOf(
        libs.h2,
        libs.sqlite,
    ).forEach { implementation(it) }
    sequenceOf(
        libs.mysql,
        libs.mariadb,
        libs.postgresql
    ).forEach { runtimeOnly(it) }

    sequenceOf(
        libs.configurate.core,
        libs.configurate.yaml,
        libs.configurate.hocon,
        libs.configurate.gson
    ).forEach { implementation(it) }
}

tasks {
    shadowJar {
        archiveBaseName.set("ImpactorAPI-Storage")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")
    }
}

artifacts {
    archives(tasks.shadowJar)
}

