@file:Suppress("unused")

package me.jakejmattson.discordkt.api.extensions

import dev.kord.rest.builder.message.EmbedBuilder

/**
 * Utility function to create a field.
 */
fun EmbedBuilder.addField(name: String, value: String) = field {
    this.name = name.validOrBlank()
    this.value = value.validOrBlank()
    this.inline = false
}

/**
 * Utility function to create an inline field.
 */
fun EmbedBuilder.addInlineField(name: String, value: String) = field {
    this.name = name.validOrBlank()
    this.value = value.validOrBlank()
    this.inline = true
}

private fun String.validOrBlank() = ifBlank { "\u200E" } //Zero-width Space