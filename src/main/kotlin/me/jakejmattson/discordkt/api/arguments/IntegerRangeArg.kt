package me.jakejmattson.discordkt.api.arguments

import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.dsl.internalLocale
import me.jakejmattson.discordkt.api.locale.inject

/**
 * Accepts an integer within a pre-defined range.
 */
open class IntegerRangeArg(private val min: Int, private val max: Int, override val name: String = "Integer ($min-$max)") : ArgumentType<Int> {
    override val description = internalLocale.integerRangeArgDescription.inject(min.toString(), max.toString())

    init {
        require(max > min) { "Maximum value must be greater than minimum value." }
    }

    override suspend fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Int> {
        val int = arg.toIntOrNull()
            ?: return Error(internalLocale.invalidFormat)

        if (int !in min..max)
            return Error("Not in range $min-$max")

        return Success(int)
    }

    override suspend fun generateExamples(event: CommandEvent<*>) = listOf((min..max).random().toString())
}