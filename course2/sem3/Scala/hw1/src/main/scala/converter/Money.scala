package converter

import converter.errors._

case class Money private(amount: BigDecimal, currency: String) {
  def +(that: Money): Money = {
    if (this.currency != that.currency) throw new WrongCurrencyException
    Money(this.amount + that.amount, this.currency)
  }

  def -(that: Money): Money = {
    if (this.currency != that.currency) throw new WrongCurrencyException
    if (this.amount < that.amount) throw new MoneyAmountShouldBePositiveException
    Money(this.amount - that.amount, this.currency)
  }

  def isSameCurrency(that: Money): Boolean = this.currency == that.currency
}

object Money {

  import Currencies.SupportedCurrencies

  def apply(amount: BigDecimal, currency: String): Money = {
    if (amount < 0) throw new MoneyAmountShouldBePositiveException
    if (!SupportedCurrencies.contains(currency)) throw new UnsupportedCurrencyException
    new Money(amount, currency)
  }
}

