import extensions.moduleVersion

plugins {
    id("impactor.api-conventions")
    id("impactor.base-conventions")
}

version = moduleVersion()

dependencies {
    implementation(projects.loader)

    // Adventure / Kyori
    api(libs.adventure.api)
    api(libs.adventure.key)

    // Google
    api(libs.google.gson)
    api(libs.google.guava)
    api(libs.google.guice)
    api(libs.google.auto.service)
    annotationProcessor(libs.google.auto.service)

    // Caffeine
    api(libs.caffeine.core)

    // Logging
    api(libs.log4j.api)

    // JUnit Testing
    testImplementation(libs.bundles.log4j)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}