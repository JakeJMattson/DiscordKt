package me.aberrantfox.kjdautils.internal.arguments

import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*

open class CharArg(override val name : String = "Character"): ArgumentType<Char>() {
    companion object : CharArg()

    override val examples = arrayListOf("a", "b", "c")
    override val consumptionType = ConsumptionType.Single
    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Char> {
        return if (arg.length == 1)
            ArgumentResult.Success(arg[0])
        else
            ArgumentResult.Error("Invalid character argument.")
    }
}