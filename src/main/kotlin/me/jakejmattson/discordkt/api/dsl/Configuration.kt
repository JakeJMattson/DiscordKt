@file:Suppress("unused")

package me.jakejmattson.discordkt.api.dsl

import dev.kord.core.enableEvent
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.x.emoji.DiscordEmoji
import dev.kord.x.emoji.Emojis
import me.jakejmattson.discordkt.internal.annotations.NestedDSL
import java.awt.Color

/**
 * Contains all properties configured when the bot is created.
 *
 * @property packageName The detected package name for the bot.
 * @property allowMentionPrefix Uses the bot mention (@Bot) as a prefix.
 * @property showStartupLog Displays log information when the bot starts.
 * @property generateCommandDocs Generates a markdown file of command info.
 * @property recommendCommands Recommends the closest command name when an invalid one is attempted.
 * @property enableSearch Allows searching for a command by typing 'search <command name>'.
 * @property commandReaction The reaction added to a message when a command is received.
 * @property theme The color theme of internal embeds (i.e. Help).
 * @property intents Additional gateway intents to register manually.
 * @property entitySupplyStrategy [EntitySupplyStrategy] for use in Kord cache.
 */
data class BotConfiguration(
    val packageName: String,
    val allowMentionPrefix: Boolean,
    val showStartupLog: Boolean,
    val generateCommandDocs: Boolean,
    val recommendCommands: Boolean,
    val enableSearch: Boolean,
    val commandReaction: DiscordEmoji?,
    val theme: Color?,
    val intents: MutableSet<Intent>,
    val entitySupplyStrategy: EntitySupplyStrategy<*>,

    internal val prefix: suspend (DiscordContext) -> String,
    internal val mentionEmbed: (suspend EmbedBuilder.(DiscordContext) -> Unit)?,
) {
    @PublishedApi
    internal inline fun <reified T : Event> enableEvent() {
        intents.addAll(
            Intents {
                this.enableEvent<T>()
            }.values
        )
    }
}

/**
 * Holds all basic configuration options.
 *
 * @property allowMentionPrefix Uses the bot mention (@Bot) as a prefix.
 * @property showStartupLog Displays log information when the bot starts.
 * @property generateCommandDocs Generates a markdown file of command info.
 * @property recommendCommands Recommends the closest command name when an invalid one is attempted.
 * @property enableSearch Allows searching for a command by typing 'search <command name>'.
 * @property commandReaction The reaction added to a message when a command is received.
 * @property theme The color theme of internal embeds (i.e. Help).
 * @property intents Additional gateway intents to register manually.
 * @property entitySupplyStrategy [EntitySupplyStrategy] for use in Kord cache.
 */
data class SimpleConfiguration(var allowMentionPrefix: Boolean = true,
                               var showStartupLog: Boolean = true,
                               var generateCommandDocs: Boolean = true,
                               var recommendCommands: Boolean = true,
                               var enableSearch: Boolean = true,
                               var commandReaction: DiscordEmoji? = Emojis.eyes,
                               var theme: Color? = null,
                               var intents: Set<Intent> = setOf(),
                               var entitySupplyStrategy: EntitySupplyStrategy<*> = EntitySupplyStrategy.cacheWithCachingRestFallback) {
    @PublishedApi
    internal var permissionLevels: List<Enum<*>> = listOf(DefaultPermissions.EVERYONE)

    @PublishedApi
    internal var commandDefault: Enum<*> = DefaultPermissions.EVERYONE

    /**
     * Configure permissions for this bot with an enum that inherits from [PermissionSet].
     * @sample DefaultPermissions
     *
     * @param commandDefault The default permission that all commands require.
     */
    @NestedDSL
    inline fun <reified T : Enum<T>> permissions(commandDefault: Enum<T>) {
        this.permissionLevels = enumValues<T>().toList()
        this.commandDefault = commandDefault
    }
}

private enum class DefaultPermissions : PermissionSet {
    EVERYONE {
        override suspend fun hasPermission(context: PermissionContext) = true
    }
}