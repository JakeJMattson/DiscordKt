package me.jakejmattson.discordkt.internal.listeners

import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.addReaction
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.TypeContainer
import me.jakejmattson.discordkt.api.conversations.Conversations
import me.jakejmattson.discordkt.api.dsl.*
import me.jakejmattson.discordkt.api.extensions.trimToID
import me.jakejmattson.discordkt.internal.command.stripMentionInvocation
import me.jakejmattson.discordkt.internal.command.stripPrefixInvocation
import me.jakejmattson.discordkt.internal.utils.Recommender

internal suspend fun registerCommandListener(discord: Discord) = discord.kord.on<MessageCreateEvent> {
    val config = discord.configuration
    val self = kord.selfId.value
    val author = message.author ?: return@on
    val discordContext = DiscordContext(discord, message, getGuild())
    val prefix = config.prefix.invoke(discordContext)
    val channel = message.channel.asChannel()
    val content = message.content

    fun String.isBotMention() = config.allowMentionPrefix && (startsWith("<@!$self>") || startsWith("<@$self>"))
    fun String.isSearch() = config.enableSearch && lowercase().startsWith("search") && split(" ").size == 2
    suspend fun search() {
        val query = content.split(" ")[1]

        if (discord.commands[query] != null)
            message.addReaction(Emojis.whiteCheckMark)
    }

    val rawInputs = when {
        content.startsWith(prefix) -> stripPrefixInvocation(content, prefix)
        content.trimToID() == self.toString() -> {
            config.mentionEmbed?.let {
                channel.createEmbed {
                    it.invoke(this, discordContext)
                }
            }

            return@on
        }
        content.isBotMention() -> stripMentionInvocation(content)
        content.isSearch() -> return@on search()
        else -> return@on Conversations.handleMessage(message)
    }

    val (_, commandName, _) = rawInputs

    if (commandName.isBlank()) return@on

    val event = getGuild()?.let {
        GuildCommandEvent<TypeContainer>(rawInputs, discord, message, author, channel as TextChannel, it)
    } ?: DmCommandEvent(rawInputs, discord, message, author, channel as DmChannel)

    if (!arePreconditionsPassing(event)) return@on

    val command = discord.commands[commandName]?.takeUnless { !it.hasPermissionToRun(event) }
        ?: return@on Recommender.sendRecommendation(event, commandName)

    config.commandReaction?.let {
        message.addReaction(it)
    }

    command.invoke(event, rawInputs.commandArgs)
}

internal suspend fun arePreconditionsPassing(event: CommandEvent<*>): Boolean {
    event.discord.preconditions
        .sortedBy { it.priority }
        .forEach { precondition ->
            try {
                precondition.check(event)
            } catch (e: Exception) {
                e.message.takeUnless { it.isNullOrEmpty() }?.let { event.respond(it) }
                return false
            }
        }

    return true
}