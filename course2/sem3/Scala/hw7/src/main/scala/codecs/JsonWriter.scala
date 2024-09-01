package codecs
import Person._
import Json._
import cats.Eval

trait JsonWriter[A] {
  def write(a: A): Json
}

object JsonWriter {
  // Summoner function
  def apply[A](implicit writer: JsonWriter[A]): JsonWriter[A] = writer

  implicit class JsonWriterOps[A](val a: A) extends AnyVal {
    def toJson(implicit writer: JsonWriter[A]): Json = writer.write(a)
  }

  implicit class ContravariantJsonWriterOps[A](writer: JsonWriter[A]) {
    def contramap[B <: A](func: B => A): JsonWriter[B] = (value: B) => writer.write(func(value))
  }

  implicit def subTypeWriter[A <: Person](implicit writer: JsonWriter[Person]): JsonWriter[A] =
    writer.contramap(identity)

  implicit val noneWriter: JsonWriter[None.type] = _ => Json.JsonNull
  implicit val stringWriter: JsonWriter[String] = (a: String) => Json.JsonString(a)
  implicit val intWriter: JsonWriter[Int] = (a: Int) => Json.JsonInt(a)
  implicit val doubleWriter: JsonWriter[Double] = (a: Double) => Json.JsonDouble(a)
  implicit def listWriter[A](implicit writer: JsonWriter[A]): JsonWriter[List[A]] = (a: List[A]) =>
    Json.JsonArray(a.map(writer.write))
  implicit def optionWriter[A](implicit writer: JsonWriter[A]): JsonWriter[Option[A]] = {
    case Some(value) => writer.write(value)
    case None        => Json.JsonNull
  }

  implicit val universityWriter: JsonWriter[University] = (u: University) => {
    JsonObject(
      Map(
        "name" -> JsonString(u.name),
        "city" -> JsonString(u.city),
        "country" -> JsonString(u.country),
        "qsRank" -> JsonInt(u.qsRank)
      )
    )
  }

  implicit val studentWriter: JsonWriter[Student] = (s: Student) => {
    JsonObject(
      Map(
        "name" -> JsonString(s.name),
        "age" -> JsonInt(s.age),
        "university" -> s.university.toJson
      )
    )
  }

  implicit val employeeWriter: JsonWriter[Employee] = (e: Employee) => {
    Json.JsonObject(
      Map(
        "name" -> Json.JsonString(e.name),
        "age" -> Json.JsonInt(e.age),
        "salary" -> Json.JsonDouble(e.salary)
      )
    )
  }

  implicit val managerWriter: JsonWriter[Manager] = (m: Manager) => {
    Json.JsonObject(
      Map(
        "name" -> Json.JsonString(m.name),
        "age" -> Json.JsonInt(m.age),
        "salary" -> Json.JsonDouble(m.salary),
        "employees" -> Json.JsonArray(m.employees.map(_.toJson))
      ) ++ m.boss.map(boss => "boss" -> Eval.later(managerWriter.write(boss)).value)
    )
  }

  implicit val personWriter: JsonWriter[Person] = (p: Person) => {
    JsonObject(
      Map(
        "name" -> JsonString(p.name),
        "age" -> JsonInt(p.age)
      )
    )
  }

}
