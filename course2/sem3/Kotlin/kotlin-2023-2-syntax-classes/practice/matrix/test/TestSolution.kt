import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class TestSolution : BehaviorSpec({
    Given("IntMatrix class implementation") {
        When("call constructor with positive args") {
            Then("ok") {
                shouldNotThrowAny { IntMatrix(rows = 10, columns = 10) }
            }
        }
        When("call with negative args") {
            Then("get exception") {
                shouldThrow<IllegalArgumentException> { IntMatrix(rows = -10, columns = 10) }
                shouldThrow<IllegalArgumentException> { IntMatrix(rows = 10, columns = -10) }
                shouldThrow<IllegalArgumentException> { IntMatrix(rows = -10, columns = -10) }
            }
        }
    }

    Given("IntMatrix instance") {
        val matrix = IntMatrix(rows = 10, columns = 20)

        When("call matrix.rows or matrix.columns") {
            Then("get correct count") {
                matrix.rows shouldBe 10
                matrix.columns shouldBe 20
            }
        }

        When("use value by correct index") {
            Then("get correct value") {
                matrix[0, 0] shouldBe 0
            }
        }

        When("set value by correct index") {
            Then("get correct new value") {
                matrix[0, 0] = 10
                matrix[0, 0] shouldBe 10
            }
        }

        When("use or set value by incorrect index") {
            Then("get exception") {
                shouldThrow<IllegalArgumentException> { matrix[-1, 0] }
                shouldThrow<IllegalArgumentException> { matrix[0, -1] }
                shouldThrow<IllegalArgumentException> { matrix[20, 0] }
                shouldThrow<IllegalArgumentException> { matrix[0, 30] }

                shouldThrowUnit<IllegalArgumentException> { matrix[-1, 0] = 10 }
                shouldThrowUnit<IllegalArgumentException> { matrix[0, -1] = 10 }
                shouldThrowUnit<IllegalArgumentException> { matrix[20, 0] = 10 }
                shouldThrowUnit<IllegalArgumentException> { matrix[0, 30] = 10 }
            }
        }
    }
})
