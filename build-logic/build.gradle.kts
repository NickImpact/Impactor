plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.architectury.dev/")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.licenser)
    implementation(libs.blossom)
    implementation(libs.shadow)
    implementation(libs.loom)
    implementation(libs.architecturyPlugin)
}