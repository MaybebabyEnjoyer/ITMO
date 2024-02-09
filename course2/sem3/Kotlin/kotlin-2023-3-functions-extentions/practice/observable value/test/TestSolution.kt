import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class TestSolution : BehaviorSpec({
    Given("MutableValue implementation") {
        When("create mutable instance") {
            val mutableValue: MutableValue<String> = MutableValue(initial = "initial")
            Then("can read value") {
                mutableValue.value shouldBe "initial"
            }
            Then("can write value") {
                mutableValue.value = "updated"
                mutableValue.value shouldBe "updated"
            }
        }

        When("use immutable instance") {
            val value: Value<CharSequence> = MutableValue(initial = "initial")
            Then("can subscribe/unsubscribe") {
                val cancellation = value.observe { }
                cancellation.cancel()
            }
        }
    }

    Given("observer") {
        val observer = Observer()

        Then("could run example from task") {
            val mutableValue = MutableValue(initial = "initial")

            val cancellation = mutableValue.observe(observer::internalValue::set)
            observer.internalValue shouldBe "initial"

            mutableValue.value = "updated"
            observer.internalValue shouldBe "updated"

            cancellation.cancel()
            mutableValue.value = "final"
            observer.internalValue shouldBe "updated"
        }
    }

    Given("several observers") {
        val observerA = Observer()
        val observerB = Observer()

        Then("all observers will be notified") {
            val value = MutableValue(initial = "initial")

            value.observe(observerA::internalValue::set)
            observerA.internalValue shouldBe "initial"

            value.observe(observerB::internalValue::set)
            observerB.internalValue shouldBe "initial"

            value.value = "updated"
            observerA.internalValue shouldBe "updated"
            observerB.internalValue shouldBe "updated"
        }
    }
})

private class Observer {
    var internalValue: String? = null
}
