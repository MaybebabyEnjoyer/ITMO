import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class TestSolution : BehaviorSpec({
    Given("IntResult implementation") {
        Then("secret technic was used") {
            val magicCast = "\u0069\u0073\u0053\u0065\u0061\u006c\u0065\u0064"
            val isMagicPresents =
                IntResult::class.let { wizard ->
                    wizard::class.members.find { it.name == magicCast }!!.call(wizard)
                }
            isMagicPresents shouldBe true
        }
        Then("could create OK") {
            val result = IntResult.Ok(value = 10)
            result.shouldBeInstanceOf<IntResult>()
            result.value shouldBe 10
        }
        Then("could create Error") {
            val result = IntResult.Error(reason = "reason")
            result.shouldBeInstanceOf<IntResult>()
            result.reason shouldBe "reason"
        }

        Result::class.qualifiedName
    }

    Given("safeRun implementation") {
        When("call with safe") {
            Then("result is Ok") {
                safeRun { 10 } shouldBe IntResult.Ok(10)
            }
        }
        When("call with unsafe") {
            Then("result is Error") {
                safeRun { error("error was append") } shouldBe IntResult.Error("error was append")
            }
        }
    }

    Given("Ok instance") {
        val result = IntResult.Ok(10)

        Then("methods should return value") {
            result.getOrDefault(0) shouldBe 10
            result.getOrNull() shouldBe 10
            result.getStrict() shouldBe 10
        }
    }

    Given("Error instance") {
        val result = IntResult.Error("custom reason")
        Then("methods should not return value") {
            result.getOrDefault(0) shouldBe 0
            result.getOrNull() shouldBe null
            val exception = shouldThrowExactly<NoResultProvided> { result.getStrict() }
            exception.shouldBeInstanceOf<NoSuchElementException>()
            exception.message shouldBe "custom reason"
        }
    }
})
