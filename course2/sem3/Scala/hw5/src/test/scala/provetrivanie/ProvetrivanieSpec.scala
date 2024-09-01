package provetrivanie
import provetrivanie.Provetrivanie.{maxInSublists, maxInSublistsOptimized}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ProvetrivanieSpec extends AnyFlatSpec with Matchers {

  "maxInSublists" should "return correct results" in {
    maxInSublists(List(1, 2, 3, 4), 2).toOption.get shouldBe List(2, 3, 4)
    maxInSublists(List(1, 3, 5, 3, 4, 2), 3).toOption.get shouldBe List(5, 5, 5, 4)
    maxInSublists(List(1, 1, 1, 1), 2).toOption.get shouldBe List(1, 1, 1)
    maxInSublists(List(), 2).toOption.get shouldBe List()
    maxInSublists(List(4), 1).toOption.get shouldBe List(4)
  }

  it should "return error if n < k" in {
    maxInSublists(List(1, 2, 3, 4), 5).isLeft shouldBe true
    maxInSublists(List(1, 2, 3, 4), 0).isLeft shouldBe true
  }

  "maxInSublistsOptimized" should "return correct results" in {
    maxInSublistsOptimized(List(1, 2, 3, 4), 2).toOption.get shouldBe List(2, 3, 4)
    maxInSublistsOptimized(List(1, 3, 5, 3, 4, 2), 3).toOption.get shouldBe List(5, 5, 5, 4)
    maxInSublistsOptimized(List(1, 1, 1, 1), 2).toOption.get shouldBe List(1, 1, 1)
    maxInSublistsOptimized(List(), 2).toOption.get shouldBe List()
    maxInSublistsOptimized(List(4), 1).toOption.get shouldBe List(4)
  }

  it should "return error if n < k" in {
    maxInSublistsOptimized(List(1, 2, 3, 4), 5).isLeft shouldBe true
    maxInSublistsOptimized(List(1, 2, 3, 4), 0).isLeft shouldBe true
  }
}
