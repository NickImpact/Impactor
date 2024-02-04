plugins {
    id("impactor.base-conventions")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    implementation(project(":api:config"))
    implementation(project(":api:economy"))
}

tasks {
    jar {
        archiveClassifier.set("")
    }
}