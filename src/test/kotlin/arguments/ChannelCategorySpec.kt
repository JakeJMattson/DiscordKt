package arguments


import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.arguments.ChannelCategoryArg
import mock.FakeIds
import mock.GherkinMessages
import mock.attemptConvert
import mock.convertToSingle
import net.dv8tion.jda.core.entities.Category
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object ChannelCategorySpec : Spek({
    Feature("Channel Category Command Argument") {
        Scenario(GherkinMessages.ValidArgumentIsPassed) {
            Then(GherkinMessages.ConversionSucceeds) {
                assertEquals(FakeIds.Category, (ChannelCategoryArg.convertToSingle("1") as Category).id)
            }
        }

        Scenario("An invalid category is passed") {
            Then(GherkinMessages.ConversationFails) {
                assertTrue(ChannelCategoryArg.attemptConvert("2") is ArgumentResult.Error)
            }
        }

        Scenario("An empty string is passed") {
            Then(GherkinMessages.ConversationFails) {
                assertTrue(ChannelCategoryArg.attemptConvert("") is ArgumentResult.Error)
            }
        }
    }
})