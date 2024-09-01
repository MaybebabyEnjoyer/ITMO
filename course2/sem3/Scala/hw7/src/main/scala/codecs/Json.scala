package codecs

import cats.Show

sealed trait Json

object Json {

  final case object JsonNull extends Json
  final case class JsonString(value: String) extends Json
  final case class JsonInt(value: Int) extends Json
  final case class JsonDouble(value: Double) extends Json
  final case class JsonArray(value: List[Json]) extends Json
  final case class JsonObject(value: Map[String, Json]) extends Json

  private def showJson(json: Json, depth: Int = 0): String = {
    val indent = "  " * depth
    val nextIndent = "  " * (depth + 1)

    json match {
      case JsonNull      => "null"
      case JsonString(s) => s""""$s""""
      case JsonInt(i)    => i.toString
      case JsonDouble(d) => d.toString
      case JsonArray(arr) =>
        if (arr.isEmpty) "[]"
        else {
          val elements = arr.map(elem => s"$nextIndent${showJson(elem, depth + 1)}").mkString(",\n")
          s"[\n$elements\n$indent]"
        }
      case JsonObject(obj) =>
        if (obj.isEmpty) "{}"
        else {
          val elements = obj
            .map { case (k, v) =>
              s"""$nextIndent"$k": ${showJson(v, depth + 1)}"""
            }
            .mkString(",\n")
          s"{\n$elements\n$indent}"
        }
    }
  }

  implicit val show: Show[Json] = Show.show(json => showJson(json) + "\n")
}
