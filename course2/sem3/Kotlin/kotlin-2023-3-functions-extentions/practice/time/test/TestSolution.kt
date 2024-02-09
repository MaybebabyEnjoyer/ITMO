import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class TestSolution : BehaviorSpec({
    Given("Time extension function") {
        When("create time from int milliseconds") {
            Then("0.milliseconds should be 0.000") {
                0.milliseconds shouldBe Time(0, 0)
            }
            Then("777.milliseconds should be 0.777") {
                777.milliseconds shouldBe Time(0, 777)
            }
            Then("13212.milliseconds should be 13.212") {
                13212.milliseconds shouldBe Time(13, 212)
            }
        }

        When("create time from long milliseconds") {
            Then("0L.milliseconds should be 0.000") {
                0L.milliseconds shouldBe Time(0, 0)
            }
            Then("777.milliseconds should be 0.999") {
                999L.milliseconds shouldBe Time(0, 999)
            }
            Then("65323L.milliseconds should be 65.323") {
                65323L.milliseconds shouldBe Time(65, 323)
            }
        }

        When("create time from int seconds") {
            Then("0.seconds should be 0.0") {
                0.seconds shouldBe Time(0, 0)
            }
            Then("42.seconds should be 42.0") {
                42.seconds shouldBe Time(42, 0)
            }
        }

        When("create time from long seconds") {
            Then("0.seconds should be 0.0") {
                0L.seconds shouldBe Time(0, 0)
            }
            Then("42.seconds should be 42.0") {
                42L.seconds shouldBe Time(42, 0)
            }
        }


        When("create time from int minutes") {
            Then("0.minutes should be 0.0") {
                0.minutes shouldBe Time(0, 0)
            }
            Then("7.minutes should be 420.0") {
                7.minutes shouldBe Time(420, 0)
            }
        }

        When("create time from long minutes") {
            Then("0.minutes should be 0.0") {
                0L.minutes shouldBe Time(0, 0)
            }
            Then("42.minutes should be 720.0") {
                12L.minutes shouldBe Time(720, 0)
            }
        }


        When("create time from int hours") {
            Then("0.hours should be 0.0") {
                0.hours shouldBe Time(0, 0)
            }
            Then("7.hours should be 10800.0") {
                3.hours shouldBe Time(10800, 0)
            }
        }

        When("create time from long hours") {
            Then("0.hours should be 0.0") {
                0L.hours shouldBe Time(0, 0)
            }
            Then("42.hours should be 7200.0") {
                2L.hours shouldBe Time(7200, 0)
            }
        }
    }

    Given("Time arithmetic operations") {
        When("time addition") {
            Then("0.0 + 10.012 should be 10.012") {
                Time(0, 0) + Time(10, 12) shouldBe Time(10, 12)
            }
            Then("11.0 + 0.999 should be 11.999") {
                Time(11, 0) + Time(0, 999) shouldBe Time(11, 999)
            }
            Then("1.999 + 3.001 should be 5.000") {
                Time(1, 999) + Time(3, 1) shouldBe Time(5, 0)
            }
            Then("10.900 + 12.800 should be 23.700") {
                Time(10, 900) + Time(12, 800) shouldBe Time(23, 700)
            }
        }
        When("time subtraction") {
            Then("10.222 - 10.222 should be 0.000") {
                Time(10, 222) - Time(10, 222) shouldBe Time(0, 0)
            }
            Then("33.999 - 32.998 should be 0.001") {
                Time(33, 999) - Time(32, 998) shouldBe Time(1, 1)
            }
            Then("12.001 - 0.000 should be 12.001") {
                Time(12, 1) - Time(0, 0) shouldBe Time(12, 1)
            }
            Then("10.900 - 12.800 should thrown IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    Time(10, 900) - Time(12, 800) shouldBe Time(-2, 100)
                }
            }
            Then("10.000 - 12.800 should thrown IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    Time(10, 0) - Time(12, 800) shouldBe Time(-3, 200)
                }
            }
        }
    }
})
