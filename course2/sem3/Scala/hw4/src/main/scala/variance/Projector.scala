package variance

import scala.util.Random

import scala.annotation.tailrec

trait Converter[-S] {
  def convert(value: S): String
}

trait Slide[+R] {
  def read: (Option[R], Slide[R])
}

class Projector[R](converter: Converter[R]) {

  def project(screen: Slide[R]): String = {
    @tailrec
    def loop(s: Slide[R], acc: String): String = {
      s.read match {
        case (Some(token), nextSlide) => loop(nextSlide, acc + converter.convert(token))
        case (None, _)                => acc
      }
    }

    loop(screen, "")
  }
}

class WordLine(val word: String) // Trait?

class RedactedWordLine(val redactionFactor: Double, word: String) extends WordLine(word)

object LineConverter extends Converter[WordLine] {
  override def convert(value: WordLine): String = value.word + "\n"
}

object RedactedLineConverter extends Converter[RedactedWordLine] {
  override def convert(value: RedactedWordLine): String = Random.nextDouble() match {
    case x if x < value.redactionFactor => "█" * value.word.length + "\n"
    case _                              => value.word + "\n"
  }
}

class HelloSlide[R <: WordLine](val lines: Seq[R]) extends Slide[R] {

  def size: Int = lines.length

  override def read: (Option[R], Slide[R]) = lines match {
    case head :: tail => (Some(head), new HelloSlide(tail))
    case Nil          => (None, this)
  }
}
