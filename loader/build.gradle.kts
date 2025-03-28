plugins {
    id("impactor.base-conventions")
}

dependencies {
    api(libs.google.guava)
    implementation(libs.google.auto.service)

    api(libs.log4j.api)
    api(libs.jetbrains.annotations)

    // Guice
    api(libs.google.guice)

    testImplementation(libs.junit.api)
    testImplementation(libs.junit.parameters)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.log4j.core)

    testAnnotationProcessor(libs.google.auto.service)
}