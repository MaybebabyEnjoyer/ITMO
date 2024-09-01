package codecs

import cats.data.{NonEmptyList, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import codecs.Person._
import cats.Eval

trait JsonReader[A] {
  def read(json: Json): ValidatedNel[ReaderError, A]
}

object JsonReader {
  // Summoner function
  def apply[A](implicit reader: JsonReader[A]): JsonReader[A] = reader

  implicit class JsonReaderOps(val json: Json) extends AnyVal {
    def as[A](implicit reader: JsonReader[A]): ValidatedNel[ReaderError, A] = reader.read(json)
  }

  private def extractField[A](obj: Map[String, Json], fieldName: String)(implicit
    reader: JsonReader[A]
  ): ValidatedNel[ReaderError, A] =
    obj.get(fieldName).map(_.as[A]).getOrElse(Invalid(NonEmptyList.one(AbsentField(fieldName))))

  private def readPersonLikeFields(
    obj: Map[String, Json]
  ): (ValidatedNel[ReaderError, String], ValidatedNel[ReaderError, Int]) = {
    val nameValidation = extractField[String](obj, "name")
    val ageValidation = extractField[Int](obj, "age")

    (nameValidation, ageValidation)
  }

  // Basic type implementations
  implicit val stringReader: JsonReader[String] = {
    case Json.JsonString(s) => Valid(s)
    case _                  => Invalid(NonEmptyList.one(WrongType("String")))
  }
  implicit val intReader: JsonReader[Int] = {
    case Json.JsonInt(i) => Valid(i)
    case _               => Invalid(NonEmptyList.one(WrongType("Int")))
  }

  implicit val doubleReader: JsonReader[Double] = {
    case Json.JsonDouble(d) => Valid(d)
    case _                  => Invalid(NonEmptyList.one(WrongType("Double")))
  }

  implicit def listReader[A](implicit elemReader: JsonReader[A]): JsonReader[List[A]] = {
    case Json.JsonArray(arr) =>
      arr.traverse(elemReader.read)
    case _ =>
      Invalid(NonEmptyList.one(WrongType("List")))
  }

  implicit val jsonObjectReader: JsonReader[Map[String, Json]] = {
    case Json.JsonObject(obj) => Valid(obj)
    case _                    => Invalid(NonEmptyList.one(WrongType("JsonObject")))
  }

  implicit def optionReader[A](implicit reader: JsonReader[A]): JsonReader[Option[A]] = {
    case Json.JsonNull => Valid(None)
    case json          => reader.read(json).map(Some(_))
  }

  implicit val universityReader: JsonReader[University] = {
    case Json.JsonObject(obj) =>
      val nameValidation = extractField[String](obj, "name")
      val cityValidation = extractField[String](obj, "city")
      val countryValidation = extractField[String](obj, "country")
      val qsRankValidation = extractField[Int](obj, "qsRank")

      (nameValidation, cityValidation, countryValidation, qsRankValidation).mapN(University)
    case _ => Invalid(NonEmptyList.one(WrongType("University")))
  }

  implicit val studentReader: JsonReader[Student] = {
    case Json.JsonObject(obj) =>
      val (nameValidation, ageValidation) = readPersonLikeFields(obj)
      val universityValidation = extractField[University](obj, "university")

      (nameValidation, ageValidation, universityValidation).mapN(Student)
    case _ => Invalid(NonEmptyList.one(WrongType("Student")))
  }

  implicit val employeeReader: JsonReader[Employee] = {
    case Json.JsonObject(obj) =>
      val (nameValidation, ageValidation) = readPersonLikeFields(obj)
      val salaryValidation = extractField[Double](obj, "salary")

      (nameValidation, ageValidation, salaryValidation).mapN(Employee)
    case _ => Invalid(NonEmptyList.one(WrongType("Employee")))
  }

  private val lazyManagerReader: Eval[JsonReader[Manager]] = Eval.later(managerReader)
  implicit val managerReader: JsonReader[Manager] = {
    case Json.JsonObject(obj) =>
      val (nameValidation, ageValidation) = readPersonLikeFields(obj)
      val salaryValidation = extractField[Double](obj, "salary")
      val employeesValidation = extractField[List[Employee]](obj, "employees")

      val bossValidation = obj.get("boss") match {
        case Some(bossJson) => lazyManagerReader.value.read(bossJson).map(Some(_))
        case None           => Valid(None)
      }

      (nameValidation, ageValidation, salaryValidation, employeesValidation, bossValidation).mapN(Manager)
    case _ => Invalid(NonEmptyList.one(WrongType("Manager")))
  }

}
