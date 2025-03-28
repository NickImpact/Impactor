plugins {
    id("impactor.api-conventions")
    id("impactor.base-conventions")
}

dependencies {
    api(projects.api.core)
    api(projects.api.platform)
    api(libs.adventure.key)
}