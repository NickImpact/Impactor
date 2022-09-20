allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.spongepowered.org/repository/maven-snapshots") {
            name = "Sponge Snapshots"
        }
        maven("https://repo.spongepowered.org/repository/maven-releases") {
            name = "Sponge Releases"
        }
    }

    license {
        header(file("../../HEADER.txt"))
    }
}