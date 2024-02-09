import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify

class TestSolution : BehaviorSpec({
    Given("config file") {
        val configName = "config"
        val configContent =
            """
            |value = 10
            |other = stringValue
            | name with spaces   = value_with_underscore
            |name_with_underscore =   value with spaces  
            """.trimMargin()

        mockkStatic(::getResource)
        every { getResource(eq(configName)) } answers { configContent.byteInputStream() }
        every { getResource(neq(configName)) } returns null

        When("create Config instance with valid name") {
            Then("ok") {
                shouldNotThrowAny { Config(configName) }
                verify { getResource(eq(configName)) }
            }
        }

        When("create Config instance with invalid name") {
            Then("get exception") {
                shouldThrow<IllegalArgumentException> { Config("undefined") }
                verify { getResource(eq(configName)) }
            }
        }

        And("correct instance") {
            val config = Config(configName)

            When("delegate correct property to config") {
                @Suppress("LocalVariableName")
                Then("get it value") {
                    val value by config
                    val other by config
                    val `name with spaces` by config
                    val name_with_underscore by config

                    value shouldBe "10"
                    other shouldBe "stringValue"
                    `name with spaces` shouldBe "value_with_underscore"
                    name_with_underscore shouldBe "value with spaces"
                }
            }

            When("delegate incorrect property to config") {
                Then("get exception") {
                    shouldThrowUnit<IllegalArgumentException> { val invalid by config }
                }
            }
        }
    }
})
