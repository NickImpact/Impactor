plugins {
    id("impactor.base-conventions")
    id("impactor.minecraft-conventions")
}

sourceSets {
    main {
        java.srcDirs(
            "src/launcher/java",
            "src/mixins/java"
        )

        resources.srcDirs(
            "src/mixins/resources"
        )
    }
}

dependencies {
    api(projects.loader)
    api(projects.api.core)
    api(projects.api.platform)
    implementation(projects.api.text)

    api(libs.bundles.cloud.base)
    implementation(libs.bundles.mixins)
    annotationProcessor(libs.bundles.mixins)
}