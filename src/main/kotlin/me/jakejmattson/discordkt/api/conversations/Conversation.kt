package me.jakejmattson.discordkt.api.conversations

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.ComponentInteraction
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.internal.annotations.BuilderDSL

/**
 * This block builds a conversation.
 *
 * @param exitString A String entered by the user to exit the conversation.
 */
@BuilderDSL
fun conversation(exitString: String? = null, block: suspend ConversationBuilder.() -> Unit) = Conversation(exitString, block)

/**
 * A class that represent a conversation.
 *
 * @param exitString A String entered by the user to exit the conversation.
 */
class Conversation(var exitString: String? = null, private val block: suspend ConversationBuilder.() -> Unit) {
    /**
     * Start a conversation with someone in their private messages.
     *
     * @param user The user to start a conversation with.
     *
     * @return The result of the conversation indicated by an enum.
     * @sample ConversationResult
     */
    suspend inline fun startPrivately(discord: Discord, user: User): ConversationResult {
        if (user.isBot)
            return ConversationResult.INVALID_USER

        val channel = user.getDmChannel()

        if (Conversations.hasConversation(user, channel))
            return ConversationResult.HAS_CONVERSATION

        val state = ConversationBuilder(discord, user, channel, exitString)

        return start(state)
    }

    /**
     * Start a conversation with someone in a public channel.
     *
     * @param user The user to start a conversation with.
     * @param channel The guild channel to start the conversation in.
     *
     * @return The result of the conversation indicated by an enum.
     * @sample ConversationResult
     */
    suspend inline fun startPublicly(discord: Discord, user: User, channel: MessageChannel): ConversationResult {
        if (user.isBot)
            return ConversationResult.INVALID_USER

        if (Conversations.hasConversation(user, channel))
            return ConversationResult.HAS_CONVERSATION

        val state = ConversationBuilder(discord, user, channel, exitString)

        return start(state)
    }

    private lateinit var builder: ConversationBuilder

    @PublishedApi
    internal suspend fun start(conversationBuilder: ConversationBuilder): ConversationResult {
        val (_, user, channel) = conversationBuilder
        builder = conversationBuilder

        return try {
            Conversations.start(user, channel, this)
            block.invoke(conversationBuilder)
            ConversationResult.COMPLETE
        } catch (e: ExitException) {
            ConversationResult.EXITED
        } catch (e: DmException) {
            ConversationResult.CANNOT_DM
        } finally {
            Conversations.end(user, channel)
        }
    }

    @PublishedApi
    internal suspend fun acceptMessage(message: Message) = builder.acceptMessage(message)

    @OptIn(KordPreview::class)
    @PublishedApi
    internal suspend fun acceptInteraction(interaction: ComponentInteraction) = builder.acceptInteraction(interaction)
}

/**
 * An enum representing possible ways that a conversation can end.
 */
enum class ConversationResult {
    /** The target user cannot be reached - a bot or no shared guild. */
    INVALID_USER,

    /** The target user has the bot blocked or has DMs off. */
    CANNOT_DM,

    /** The target user already has a conversation. */
    HAS_CONVERSATION,

    /** The conversation has completed successfully. */
    COMPLETE,

    /** The conversation was exited by the user. */
    EXITED
}