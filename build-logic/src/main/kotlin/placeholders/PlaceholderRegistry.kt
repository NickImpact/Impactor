package placeholders

import org.gradle.api.Project

object PlaceholderRegistry {

    private val placeholders: MutableMap<String, Placeholder> = mutableMapOf()

    fun parse(project: Project, input: String): String {
        var result: String = input
        this.placeholders.forEach {
            result = result.replace("{{${it.key}}}", it.value.parser.apply(project))
        }

        return result
    }

    fun register(key: String, placeholder: Placeholder): Placeholder {
        this.placeholders[key] = placeholder
        return placeholder
    }

}