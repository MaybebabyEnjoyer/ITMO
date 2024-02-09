import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify

class TestSolution : BehaviorSpec({
    Given("BankAccount implementation") {
        When("create instance with correct amount") {
            Then("Ok") {
                shouldNotThrowAny { BankAccount(100) }
            }
        }
        When("create instance with incorrect amount") {
            Then("get exception") {
                shouldThrow<IllegalArgumentException> { BankAccount(0) }
                shouldThrow<IllegalArgumentException> { BankAccount(-100) }
            }
        }
    }

    Given("BankAccount instance") {
        mockkStatic(::logTransaction)
        every { logTransaction(any(), any()) } returns Unit

        val account = BankAccount(amount = 100)

        When("get balance") {
            Then("get initial amount") {
                account.balance shouldBe 100
            }
        }

        When("send correct deposit") {
            Then("balance must be changed") {
                account.deposit(100)
                account.balance shouldBe 200
                verify { logTransaction(eq(100), eq(200)) }
            }
        }
        When("send incorrect deposit") {
            Then("get exception") {
                shouldThrow<IllegalArgumentException> { account.deposit(0) }
                shouldThrow<IllegalArgumentException> { account.deposit(-100) }
            }
        }

        When("request correct withdraw") {
            Then("balance must be changed") {
                account.withdraw(100)
                account.balance shouldBe 100
                verify { logTransaction(eq(200), eq(100)) }
            }
        }
        When("request incorrect withdraw") {
            Then("get exception") {
                shouldThrow<IllegalArgumentException> { account.withdraw(0) }
                shouldThrow<IllegalArgumentException> { account.withdraw(-100) }
                shouldThrow<IllegalArgumentException> { account.withdraw(200) }
            }
        }
    }
})
