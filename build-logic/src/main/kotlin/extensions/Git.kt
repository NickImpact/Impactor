package extensions

import org.gradle.api.Project
import java.io.ByteArrayOutputStream

fun Project.getLatestGitCommitHash(): String {
    return try {
        val byteOut = ByteArrayOutputStream()
        val task = providers.exec {
            commandLine("git rev-parse --short HEAD".split(" "))
        }

        task.standardOutput.asText.get()
    } catch (ex: Exception) {
        "Unknown"
    }
}

fun Project.getPreviousTag(): String {
    return try
    {
        val task = providers.exec {
            commandLine("git describe --abbrev=0 --tags --exclude=${getLatestTag()}".split(" "))
        }

        task.standardOutput.asText.get()
    } catch (ex: Exception) {
        "Unknown"
    }
}

fun Project.getLatestTag(): String {
    return try
    {
        val byteOut = ByteArrayOutputStream()
        val task = providers.exec {
            commandLine("git describe --abbrev=0 --tags".split(" "))
        }

        task.standardOutput.asText.get()
    } catch (ex: Exception) {
        "Unknown"
    }
}