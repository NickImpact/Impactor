plugins {
    id("impactor.api-conventions")
    id("impactor.base-conventions")
}

dependencies {
    api(projects.api.core)
    api(projects.api.schedulers)

    annotationProcessor(libs.google.auto.service)

    implementation(libs.adventure.serializers.plain)
    compileOnly(libs.networking.jedis)
}