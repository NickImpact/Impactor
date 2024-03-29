package tasks

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

open class PublishToDiscord : DefaultTask() {

    @TaskAction
    fun run() {
        val root = this.project.rootProject
        val plugin = root.property("plugin")
        val minecraft = root.property("minecraft")

        val client = WebhookClient.withUrl(System.getenv("DISCORD_RELEASE_WEBHOOK"))
        val embed = WebhookEmbedBuilder()
            .setAuthor(WebhookEmbed.EmbedAuthor("NickImpact", "https://cdn.discordapp.com/icons/483709020914319361/11b7c9a7fbb890270363df3c534ddb17.webp?size=96", null))
            .setTitle(WebhookEmbed.EmbedTitle(
                "Impactor ${this.project.rootProject.property("plugin")}",
                "https://modrinth.com/mod/impactor"
            ))
            .setDescription("A new update has released!")
            .setTimestamp(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.of("-08:00")))
            .addField(
                WebhookEmbed.EmbedField(
                true,
                "Fabric",
                "https://modrinth.com/mod/impactor/version/$plugin+$minecraft-Fabric"
            ))
            .addField(
                WebhookEmbed.EmbedField(
                true,
                "Forge",
                "https://modrinth.com/mod/impactor/version/$plugin+$minecraft-Forge"
            ))

        client.send(embed.build()).thenAccept { println("Message sent successfully (ID: ${it.id})") }
    }

}