package converter

import converter.errors._
import org.scalatest.flatspec._
import org.scalatest.matchers.must._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.annotation.tailrec

class CurrencyConverterSpec extends AnyFlatSpec with Matchers {
  "Money constructor" should "not throw MoneyAmountShouldBePositiveException if amount is zero or positive" in {
    Money(0, "RUB")
    Money(1, "USD")
  }

  "exchange" should "convert money for supported currencies" in {
    val rates = Map(
      "USD" -> Map("RUB" -> BigDecimal(72.5), "EUR" -> BigDecimal(0.9)),
      "RUB" -> Map("USD" -> BigDecimal(1 / 72.5), "EUR" -> BigDecimal(1 / 73.5)),
      "EUR" -> Map("RUB" -> BigDecimal(73.5), "USD" -> BigDecimal(1.1))
    )

    val converter = CurrencyConverter(rates)

    val exchangedRub = converter.exchange(Money(2, "USD"), "RUB")
    val exchangedRub2 = converter.exchange(Money(2, "EUR"), "RUB")

    val exchangedUsd = converter.exchange(Money(10, "RUB"), "USD")
    val exchangedUsd2 = converter.exchange(Money(1, "EUR"), "USD")

    val exchangedEur = converter.exchange(Money(10, "RUB"), "EUR")
    val exchangedEur2 = converter.exchange(Money(1, "USD"), "EUR")


    exchangedRub.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(145).setScale(7, BigDecimal.RoundingMode.HALF_UP)
    exchangedRub.currency shouldEqual "RUB"
    exchangedRub2.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(147).setScale(7, BigDecimal.RoundingMode.HALF_UP)
    exchangedRub2.currency shouldEqual "RUB"

    exchangedUsd.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(1 / 7.25).setScale(7, BigDecimal.RoundingMode.HALF_UP)
    exchangedUsd.currency shouldEqual "USD"
    exchangedUsd2.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(1.1).setScale(7, BigDecimal.RoundingMode.HALF_UP)
    exchangedUsd2.currency shouldEqual "USD"

    exchangedEur.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(1 / 7.35).setScale(7, BigDecimal.RoundingMode.HALF_UP)
    exchangedEur.currency shouldEqual "EUR"
    exchangedEur2.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(0.9).setScale(7, BigDecimal.RoundingMode.HALF_UP)
    exchangedEur2.currency shouldEqual "EUR"
  }

  "exchange" should "throw WrongCurrencyException if currencies are the same" in {
    val rates = Map(
      "USD" -> Map("RUB" -> BigDecimal(72.5), "EUR" -> BigDecimal(0.9))
    )

    val converter = CurrencyConverter(rates)

    assertThrows[WrongCurrencyException] {
      converter.exchange(Money(2, "USD"), "USD")
    }
  }

  "converted constructor" should "throw UnsupportedCurrencyException if rates dictionary contains wrong currency" in {
    val genRandomString = () => scala.util.Random.alphanumeric.take(3).mkString
    val rates = Map() ++ (1 to 10).map(_ => genRandomString() -> Map("RUB" -> BigDecimal(85))).toMap
    assertThrows[UnsupportedCurrencyException] {
      CurrencyConverter(rates)
    }
  }

  "Money operations" should "be able to do expected operations" in {
    val moneyRub1 = Money(10, "RUB")
    val moneyRub2 = Money(20, "RUB")
    val moneyUsd1 = Money(10, "USD")

    val moneyRub3 = moneyRub1 + moneyRub2
    val moneyRub4 = moneyRub2 - moneyRub1


    moneyRub3.amount shouldEqual 30
    moneyRub3.currency shouldEqual "RUB"
    moneyRub4.amount shouldEqual 10
    moneyRub4.currency shouldEqual "RUB"
    moneyRub1.isSameCurrency(moneyRub2) shouldEqual true
    moneyRub1.isSameCurrency(moneyUsd1) shouldEqual false
  }

  "Money operations" should "throw WrongCurrencyException if currencies are different" in {
    val moneyRub1 = Money(10, "RUB")
    val moneyUsd1 = Money(10, "USD")

    assertThrows[WrongCurrencyException] {
      moneyRub1 + moneyUsd1
    }
    assertThrows[WrongCurrencyException] {
      moneyRub1 - moneyUsd1
    }
  }

  "Money operation - " should "throw MoneyAmountShouldBePositiveException if result is negative" in {
    val moneyRub1 = Money(10, "RUB")
    val moneyRub2 = Money(20, "RUB")

    assertThrows[MoneyAmountShouldBePositiveException] {
      moneyRub1 - moneyRub2
    }
  }

  "Money constructor" should "throw UnsupportedCurrencyException if currency is wrong" in {
    assertThrows[UnsupportedCurrencyException] {
      Money(10, "RUB1")
    }
  }

  "Money constructor" should "throw MoneyAmountShouldBePositiveException if amount is negative" in {
    assertThrows[MoneyAmountShouldBePositiveException] {
      Money(-10, "RUB")
    }
  }

  "exchange" should "work fast enough" in {
    val rates = Map(
      "USD" -> Map("RUB" -> BigDecimal(72.5), "EUR" -> BigDecimal(0.9)),
      "RUB" -> Map("USD" -> BigDecimal(1 / 72.5), "EUR" -> BigDecimal(1 / 73.5)),
      "EUR" -> Map("RUB" -> BigDecimal(73.5), "USD" -> BigDecimal(1.1))
    )

    val converter = CurrencyConverter(rates)

    @tailrec
    def testRecursion(i: Int): Unit = {
      if (i > 0) {
        val exchangedRub = converter.exchange(Money(2, "USD"), "RUB")
        val exchangedRub2 = converter.exchange(Money(2, "EUR"), "RUB")

        val exchangedUsd = converter.exchange(Money(10, "RUB"), "USD")
        val exchangedUsd2 = converter.exchange(Money(1, "EUR"), "USD")

        val exchangedEur = converter.exchange(Money(10, "RUB"), "EUR")
        val exchangedEur2 = converter.exchange(Money(1, "USD"), "EUR")

        exchangedRub.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(145).setScale(7, BigDecimal.RoundingMode.HALF_UP)
        exchangedRub.currency shouldEqual "RUB"
        exchangedRub2.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(147).setScale(7, BigDecimal.RoundingMode.HALF_UP)
        exchangedRub2.currency shouldEqual "RUB"

        exchangedUsd.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(1 / 7.25).setScale(7, BigDecimal.RoundingMode.HALF_UP)
        exchangedUsd.currency shouldEqual "USD"
        exchangedUsd2.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(1.1).setScale(7, BigDecimal.RoundingMode.HALF_UP)
        exchangedUsd2.currency shouldEqual "USD"

        exchangedEur.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(1 / 7.35).setScale(7, BigDecimal.RoundingMode.HALF_UP)
        exchangedEur.currency shouldEqual "EUR"
        exchangedEur2.amount.setScale(7, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(0.9).setScale(7, BigDecimal.RoundingMode.HALF_UP)
        exchangedEur2.currency shouldEqual "EUR"
        testRecursion(i - 1)
      }
    }

    testRecursion(100000)
  }

  "Money operations" should "work fast enough" in {
    val moneyRub1 = Money(10, "RUB")
    val moneyRub2 = Money(20, "RUB")
    val moneyUsd1 = Money(10, "USD")

    @tailrec
    def testRecursion(i: Int): Unit = {
      if (i > 0) {
        val moneyRub3 = moneyRub1 + moneyRub2
        val moneyRub4 = moneyRub2 - moneyRub1

        moneyRub3.amount shouldEqual 30
        moneyRub3.currency shouldEqual "RUB"
        moneyRub4.amount shouldEqual 10
        moneyRub4.currency shouldEqual "RUB"
        moneyRub1.isSameCurrency(moneyRub2) shouldEqual true
        moneyRub1.isSameCurrency(moneyUsd1) shouldEqual false
        testRecursion(i - 1)
      }
    }

    testRecursion(100000)
  }
}