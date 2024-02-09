import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class TestSolution : BehaviorSpec({
    Given("Result implementation") {
        Then("it has correct using interface") {
            Result::class.isSealed shouldBe true
            Result::class.typeParameters.size shouldBe 2
        }
        Then("could create Result.Ok") {
            val result = Result.Ok(value = 10)
            result.shouldBeInstanceOf<Result<Int, *>>()
        }
        Then("could create Result.Error") {
            val result = Result.Error(error = "failure")
            result.shouldBeInstanceOf<Result<*, String>>()
        }
    }

    Given("Instance of Result.Ok") {
        val ok = Result.Ok(10)
        @Suppress("UNUSED_VARIABLE")
        Then("could save to variable") {
            val okToo: Result.Ok<Number> = ok
            val result: Result<Number, CharSequence> = ok
        }
    }

    Given("Instance of Result.Error") {
        val error = Result.Error("failure")
        @Suppress("UNUSED_VARIABLE")
        Then("could save to variable") {
            val errorToo: Result.Error<CharSequence> = error
            val result: Result<Number, CharSequence> = error
        }
    }

    Given("Instance of result") {
        lateinit var result: Result<Int, String>
        Then("Is that okay?") {
            object {
                @Suppress("unused")
                val some: Result<Number, CharSequence> get() = result
            }
        }
    }
})
