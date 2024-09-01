package converter

import converter.errors._

class CurrencyConverter(ratesDictionary: Map[String, Map[String, BigDecimal]]) {
  def exchange(money: Money, toCurrency: String): Money = {
    if (money.currency == toCurrency) throw new WrongCurrencyException
    else {
      val rate = ratesDictionary(money.currency)(toCurrency)
      val exchangedAmount = money.amount * rate
      Money(exchangedAmount, toCurrency)
    }
  }
}

object CurrencyConverter {

  import Currencies.SupportedCurrencies

  def apply(ratesDictionary: Map[String, Map[String, BigDecimal]]) = {
    val fromCurrencies = ratesDictionary.keys
    val toCurrencies = ratesDictionary.values
    if (fromCurrencies.toSet.subsetOf(SupportedCurrencies) && toCurrencies.forall(_.keys.toSet.subsetOf(SupportedCurrencies)))
      new CurrencyConverter(ratesDictionary)
    else throw new UnsupportedCurrencyException

  }
}

