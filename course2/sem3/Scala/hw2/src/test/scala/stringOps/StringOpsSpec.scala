package stringOps

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import stringOps.StringOps.{cutString, duplicateString, reverseString, transformString}

class StringOpsSpec extends AnyFlatSpec with Matchers {
  "StringOps" should "duplicate a string" in {
    duplicateString("abc") shouldBe "abcabc"
  }

  it should "cut a string with rounding down" in {
    cutString("abc") shouldBe "a"
  }

  it should "reverse a string" in {
    reverseString("abc") shouldBe "cba"
  }

  it should "transform a string" in {
    transformString("abc", duplicateString) shouldBe "abcabc"
    transformString("abc", cutString) shouldBe "a"
    transformString("abc", reverseString) shouldBe "cba"
  }

  it should "be a closure" in {
    val f1 = transformString("abc", _)
    f1(duplicateString) shouldBe "abcabc"
    f1(cutString) shouldBe "a"
    f1(reverseString) shouldBe "cba"
  }
}
