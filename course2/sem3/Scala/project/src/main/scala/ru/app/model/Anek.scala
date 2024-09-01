package ru.app.model

import sttp.tapir.Schema
import tethys.{JsonReader, JsonWriter}
import tethys.derivation.semiauto.{jsonReader, jsonWriter}

case class Anek(
  id: Int,
  data: String,
  likes: Int
)

object Anek {
  implicit val schema: Schema[Anek] =
    Schema
      .derived[Anek]
      .modify(_.id)(_.description("Номер на банеках"))
      .modify(_.data)(_.description("Сам анек"))
      .modify(_.likes)(_.description("Лайки"))

  implicit val anekWriter: JsonWriter[Anek] = jsonWriter
  implicit val anekReader: JsonReader[Anek] = jsonReader
}
