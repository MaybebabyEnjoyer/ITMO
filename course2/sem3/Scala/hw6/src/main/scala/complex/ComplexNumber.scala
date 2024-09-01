package complex

import scala.language.implicitConversions
import scala.util.Try
import scala.util.matching.Regex

// DO NOT CHANGE ANYTHING BELOW
final case class ComplexNumber(real: Double, imaginary: Double) {
  def *(other: ComplexNumber) =
    ComplexNumber(
      (real * other.real) - (imaginary * other.imaginary),
      (real * other.imaginary) + (imaginary * other.real)
    )

  def +(other: ComplexNumber) =
    ComplexNumber(real + other.real, imaginary + other.imaginary)

  def ~=(o: ComplexNumber) =
    (real - o.real).abs < 1e-6 && (imaginary - o.imaginary).abs < 1e-6
}

object ComplexNumber

// DO NOT CHANGE ANYTHING ABOVE
object ComplexNumberImplicits {
  implicit class imgNum(n: Int) {
    def i: ComplexNumber = ComplexNumber(0, n)
  }

  implicit class ComplexNumberOps(val c: ComplexNumber) extends AnyVal {
    def -(other: ComplexNumber): ComplexNumber = {
      ComplexNumber(c.real - other.real, c.imaginary - other.imaginary)
    }

    def /(other: ComplexNumber): ComplexNumber = {
      val denominator = other.real * other.real + other.imaginary * other.imaginary
      if (denominator == 0) throw new IllegalArgumentException("Division by zero")
      ComplexNumber(
        (c.real * other.real + c.imaginary * other.imaginary) / denominator,
        (c.imaginary * other.real - c.real * other.imaginary) / denominator
      )
    }

    def polarForm: (Double, Double) = {
      val r = Math.sqrt(c.real * c.real + c.imaginary * c.imaginary)
      val t = Math.atan2(c.imaginary, c.real)
      (r, t)
    }
  }

  implicit object ComplexNumberNumeric extends Numeric[ComplexNumber] {
    override def plus(x: ComplexNumber, y: ComplexNumber): ComplexNumber = x + y

    override def minus(x: ComplexNumber, y: ComplexNumber): ComplexNumber = ComplexNumberOps(x) - y

    override def times(x: ComplexNumber, y: ComplexNumber): ComplexNumber = x * y

    override def negate(x: ComplexNumber): ComplexNumber = ComplexNumber(-x.real, -x.imaginary)

    override def fromInt(x: Int): ComplexNumber = ComplexNumber(x, 0)

    override def toInt(x: ComplexNumber): Int = x.real.toInt

    override def toLong(x: ComplexNumber): Long = x.real.toLong

    override def toFloat(x: ComplexNumber): Float = x.real.toFloat

    override def toDouble(x: ComplexNumber): Double = x.real

    override def compare(x: ComplexNumber, y: ComplexNumber): Int =
      if (x.real == y.real && x.imaginary == y.imaginary) 0 else if (x.real > y.real) 1 else -1

    def parseString(str: String): Option[ComplexNumber] = {
      val complexNumberPattern: Regex = """^([-+]?\d*\.?\d+)([-+]\d*\.?\d+i)$""".r
      str match {
        case complexNumberPattern(realPart, imaginaryPart) =>
          for {
            real <- Try(realPart.toDouble).toOption
            imaginary <- Try(imaginaryPart.dropRight(1).toDouble).toOption
          } yield ComplexNumber(real, imaginary)
        case _ => None
      }
    }
  }

  implicit def toComplex(s: String): ComplexNumber =
    Numeric[ComplexNumber].parseString(s).getOrElse(ComplexNumber(0, 0))
}


object Tester extends App {

  import complex.ComplexNumberImplicits._

  val n = implicitly[Numeric[ComplexNumber]]

  val z1 = ComplexNumber(2, 3)
  val z2 = ComplexNumber(1, 4)
  val z4: ComplexNumber = 2.i
  val z5 = n.fromInt(5)
  println(z4)
}