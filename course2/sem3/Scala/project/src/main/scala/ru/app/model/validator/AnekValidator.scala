package ru.app.model.validator

import ru.app.model.Config

object AnekValidator {
  def isValidAnekContent(htmlContent: String): Boolean = {
    !htmlContent.contains(Config.ErrorMessagesConfig.pageNotFound) && htmlContent.nonEmpty
  }
}
