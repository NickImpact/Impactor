plugins {
    id("impactor.base-conventions")
}

dependencies {
    api(projects.api.core)
    api(projects.api.platform)

    api(libs.adventure.api)
    api(libs.adventure.key)
    api(libs.adventure.minimessage)
    api(libs.adventure.serializers.legacy)
    api(libs.adventure.serializers.gson)

    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.log4j.api)
    testImplementation(libs.log4j.core)
    testImplementation(libs.adventure.serializers.ansi)
}