plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
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
    implementation(libs.minotaur)
    implementation(libs.architecturyPlugin)

    implementation("net.dv8tion:JDA:5.0.0-beta.19") {
        exclude(module = "opus-java")
    }
    implementation("club.minnced:discord-webhooks:0.8.4")
}