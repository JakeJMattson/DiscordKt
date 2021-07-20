@file:Suppress("unused")

package me.jakejmattson.discordkt.api.extensions

import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.flow.filter

/**
 * Send the user a private string message.
 */
suspend fun UserBehavior.sendPrivateMessage(message: String) = getDmChannel().createMessage(message)

/**
 * Send the user a private embed message.
 */
suspend fun UserBehavior.sendPrivateMessage(embed: suspend EmbedBuilder.() -> Unit) = getDmChannel().createEmbed { embed.invoke(this) }

/**
 * Get guilds shared between the user and the bot.
 */
val UserBehavior.mutualGuilds
    get() = kord.guilds.filter { it.getMemberOrNull(id) != null }

/**
 * A Discord profile link for this user.
 */
val UserBehavior.profileLink
    get() = "https://discordapp.com/users/${id.value}/"

/**
 * Checks whether this user is the bot.
 */
fun UserBehavior.isSelf() = id == kord.selfId

/**
 * Checks whether this member is the bot.
 */
fun MemberBehavior.isSelf() = id == kord.selfId
