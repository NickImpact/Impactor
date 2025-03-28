plugins {
    id("impactor.base-conventions")
}

dependencies {
    api(projects.loader)
    api(projects.api.core)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}