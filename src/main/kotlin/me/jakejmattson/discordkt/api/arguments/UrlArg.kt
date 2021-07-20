package me.jakejmattson.discordkt.api.arguments

import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.dsl.internalLocale
import me.jakejmattson.discordkt.api.extensions.containsURl

/**
 * Accepts a string that matches the URL regex.
 */
open class UrlArg(override val name: String = "URL") : ArgumentType<String> {
    /**
     * Accepts a string that matches the URL regex.
     */
    companion object : UrlArg()

    override val description = internalLocale.urlArgDescription

    override suspend fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<String> {
        return if (arg.containsURl())
            Success(arg)
        else
            Error(internalLocale.invalidFormat)
    }

    override suspend fun generateExamples(event: CommandEvent<*>) = listOf("https://www.google.com")
}