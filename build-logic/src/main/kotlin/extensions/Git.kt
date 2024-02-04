package extensions

import org.gradle.api.Project
import java.io.ByteArrayOutputStream

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

fun Project.getPreviousTag(): String {
    return try
    {
        val byteOut = ByteArrayOutputStream()
        val error = ByteArrayOutputStream()
        project.exec {
            this.commandLine = "git describe --abbrev=0 --tags --exclude=${getLatestTag()}".split(" ")
            this.standardOutput = byteOut
            this.errorOutput = error
        }

        byteOut.toString("UTF-8").trim()
    } catch (ex: Exception) {
        "Unknown"
    }
}

fun Project.getLatestTag(): String {
    return try
    {
        val byteOut = ByteArrayOutputStream()
        project.exec {
            this.commandLine = "git describe --abbrev=0 --tags".split(" ")
            this.standardOutput = byteOut
        }

        byteOut.toString("UTF-8").trim()
    } catch (ex: Exception) {
        "Unknown"
    }
}