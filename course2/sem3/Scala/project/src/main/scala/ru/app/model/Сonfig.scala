package ru.app.model

import pureconfig.ConfigSource
import pureconfig.generic.auto._
case class CssSelectorsConfig(likesElement: String)
case class ErrorMessagesConfig(pageNotFound: String)
case class PortConfig(port: Int = 8080)

object Config {
  val LikesConfig: CssSelectorsConfig = ConfigSource.default.load[CssSelectorsConfig] match {
    case Right(cfg) => cfg
    case Left(error) =>
      throw new Exception("Failed to load configuration: " + error.prettyPrint())
  }

  val ErrorMessagesConfig: ErrorMessagesConfig = ConfigSource.default.load[ErrorMessagesConfig] match {
    case Right(cfg) => cfg
    case Left(error) =>
      throw new Exception("Failed to load configuration: " + error.prettyPrint())
  }

  val PortConfig: PortConfig = ConfigSource.default.load[PortConfig] match {
    case Right(cfg) => cfg
    case Left(error) =>
      throw new Exception("Failed to load configuration: " + error.prettyPrint())
  }
}
