package me.jakejmattson.discordkt.api.arguments

import dev.kord.core.entity.Attachment
import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.dsl.internalLocale

/**
 * Accepts a file as a message attachment.
 */
open class AttachmentArg(override val name: String = "File") : ArgumentType<Attachment> {
    /**
     * Accepts a file as a message attachment.
     */
    companion object : AttachmentArg()

    override val description = internalLocale.attachmentArgDescription

    override suspend fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Attachment> {
        val attachments = event.message.attachments

        if (attachments.isEmpty())
            return Error("No attachments")

        return Success(attachments.first(), 0)
    }

    override suspend fun generateExamples(event: CommandEvent<*>) = listOf("File")
    override fun formatData(data: Attachment) = data.filename
}