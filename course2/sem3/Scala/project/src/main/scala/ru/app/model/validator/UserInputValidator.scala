package ru.app.model.validator

import sttp.tapir.Validator

object UserInputValidator {
  val positiveIntVal: Validator[Int] = Validator.min(1)
}
