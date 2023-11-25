package placeholders

import extensions.getLatestGitCommitHash
import extensions.getLatestTag
import extensions.getPreviousTag
import extensions.writeVersion
import org.gradle.api.Project
import java.util.function.Function

data class Placeholder(val parser: Function<Project, String>) {

    companion object {

        val VERSION = PlaceholderRegistry.register("version", Placeholder { it.writeVersion() })
        val GIT_COMMIT = PlaceholderRegistry.register("commit", Placeholder { it.getLatestGitCommitHash() })
        val PREVIOUS_TAG = PlaceholderRegistry.register("tags:previous", Placeholder { it.getPreviousTag() })
        val LATEST_TAG = PlaceholderRegistry.register("tags:latest", Placeholder { it.getLatestTag() })

    }

}