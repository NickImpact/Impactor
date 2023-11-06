import org.gradle.api.Project
import java.io.ByteArrayOutputStream

fun Project.writeVersion(): String {
    val plugin = rootProject.property("plugin")
    val minecraft = rootProject.property("minecraft")
    val snapshot = rootProject.property("snapshot") == "true"
    val rc = Integer.parseInt(rootProject.property("release-candidate").toString())

    var version = "$plugin+$minecraft"
    if(snapshot) {
        version = "$version-SNAPSHOT"
    } else {
        if(rc > 0) {
            version = "$version-RC$rc"
        }
    }

    return version
}

fun Project.getLatestGitCommitHash(): String {
    return try {
        val byteOut = ByteArrayOutputStream()
        project.exec {
            this.commandLine = "git rev-parse --short HEAD".split(" ")
            this.standardOutput = byteOut
        }

        byteOut.toString("UTF-8").trim()
    } catch (ex: Exception) {
        "Unknown"
    }
}

fun Project.isSnapshot(): Boolean {
    return rootProject.property("snapshot") == "true"
}

fun Project.isReleaseCandidate(): Boolean {
    val rc = Integer.parseInt(rootProject.property("release-candidate").toString())
    return rc > 0
}

fun Project.isRelease(): Boolean {
    return !this.isSnapshot() && !this.isReleaseCandidate()
}