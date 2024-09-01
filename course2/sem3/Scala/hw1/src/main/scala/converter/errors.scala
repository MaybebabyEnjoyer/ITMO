package converter

object errors {
  class MoneyAmountShouldBePositiveException extends Exception

  class UnsupportedCurrencyException extends Exception

  class WrongCurrencyException extends Exception
}
