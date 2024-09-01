package ru.app.error

import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonObjectWriter, JsonReader}

sealed trait ApiError extends Exception

final case class ServerApiError(
  message: String
) extends ApiError

object ApiError {

  implicit val serverErrorWriter: JsonObjectWriter[ServerApiError] = jsonWriter
  implicit val serverErrorReader: JsonReader[ServerApiError] = jsonReader
  implicit val serverErrorSchema: Schema[ServerApiError] =
    Schema
      .derived[ServerApiError]
      .modify(_.message)(_.description("Сообщение об ошибке"))
      .description("Внутренняя ошибка")
}
