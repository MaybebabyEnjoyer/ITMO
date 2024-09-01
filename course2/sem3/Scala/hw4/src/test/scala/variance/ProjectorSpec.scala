package variance

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ProjectorSpec extends AnyFlatSpec with Matchers {

  "Projector for RedactedWordLine" should "project Slide[RedactedWordLine] with Converter[RedactedWordLine]" in {
    val projector = new Projector[RedactedWordLine](RedactedLineConverter)
    val slide = new HelloSlide(List(new RedactedWordLine(0, "Hello"), new RedactedWordLine(1, "World")))
    val result = projector.project(slide)
    result should (include("Hello\n") and include("█" * 5 + "\n"))
  }

  it should "return type error for Slide[WordLine]" in {
    val projector = new Projector[RedactedWordLine](RedactedLineConverter)
    val slide = new HelloSlide(List(new WordLine("Hello"), new WordLine("World")))
    assertTypeError("projector.project(slide)")
  }

  "Projector for WordLine" should "project Slide[WordLine]" in {
    val projector = new Projector[WordLine](LineConverter)
    val slide = new HelloSlide(List(new WordLine("Hello"), new WordLine("World")))
    val result = projector.project(slide)
    result shouldBe "Hello\nWorld\n"
  }

  it should "project Slide[RedactedWordLine]" in {
    val projector = new Projector[WordLine](LineConverter)
    val slide = new HelloSlide(List(new RedactedWordLine(0, "Hello"), new RedactedWordLine(0, "World")))
    val result = projector.project(slide)
    result shouldBe "Hello\nWorld\n"
  }

  it should "return type error for Projector[WordLine] with Converter[RedactedWordLine]" in {
    assertTypeError("new Projector[WordLine](RedactedLineConverter)")
  }

  "RedactedWordLine" should "redact word when redactionFactor is 0" in {
    val redactedWord = new RedactedWordLine(0, "Hello")
    LineConverter.convert(redactedWord)
    RedactedLineConverter.convert(redactedWord) shouldBe "Hello\n"
  }

  "HelloSlide" should "return correct size for HelloSlide" in {
    val slide = new HelloSlide(List(new WordLine("Hello"), new WordLine("World")))
    slide.size shouldBe 2
  }

}
