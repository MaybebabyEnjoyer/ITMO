package converter

object main extends App {
        val rates = Map(
      "USD" -> Map("RUB" -> BigDecimal(72.0), "EUR" -> BigDecimal(0.9)),
      "RUB" -> Map("USD" -> BigDecimal(1/72.0), "EUR" -> BigDecimal(1/73.1)),
      "EUR" -> Map("RUB" -> BigDecimal(73.1), "USD" -> BigDecimal(1.1))
    )

    val a = CurrencyConverter(rates)
    val a1 = a.exchange(Money(2, "RUB"), "USD")
    println(a1)

}
