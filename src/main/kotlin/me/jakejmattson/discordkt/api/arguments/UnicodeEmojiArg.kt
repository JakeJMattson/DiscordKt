package me.jakejmattson.discordkt.api.arguments

import dev.kord.x.emoji.DiscordEmoji
import dev.kord.x.emoji.Emojis
import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.dsl.internalLocale

/**
 * Accepts a unicode emoji.
 */
open class UnicodeEmojiArg(override val name: String = "Emoji") : ArgumentType<DiscordEmoji> {
    /**
     * Accepts a unicode emoji.
     */
    companion object : UnicodeEmojiArg()

    override val description = internalLocale.unicodeEmojiArgDescription

    override suspend fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<DiscordEmoji> {
        val emoji = Emojis[arg.trim()] ?: return Error(internalLocale.invalidFormat)
        return Success(emoji)
    }

    override suspend fun generateExamples(event: CommandEvent<*>): List<String> = listOf(Emojis.rainbow.unicode)
}