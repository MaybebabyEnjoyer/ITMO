package collections

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CollectionsSpec extends AnyFlatSpec with Matchers {

  "findGaps" should "return None for a sequence with no gaps" in {
    Collections.findGaps(Seq(1, 2, 3, 4)) shouldBe None
  }

  it should "return gaps for sequences with gaps" in {
    Collections.findGaps(Seq(1, 2, 8)) shouldBe Some(Seq((2, 8)))
    Collections.findGaps(Seq(3, 5, 7)) shouldBe Some(Seq((3, 5), (5, 7)))
  }

  "minFold" should "return minimum value from the map" in {
    Collections.minFold(Map("a" -> 3, "b" -> 2, "c" -> 4)) shouldBe Some(("b", 2))
    Collections.minFold(Map.empty[String, Int]) shouldBe None
  }

  "minReduce" should "return minimum value from the map" in {
    Collections.minReduce(Map("a" -> 3, "b" -> 2, "c" -> 4)) shouldBe Some(("b", 2))
    Collections.minReduce(Map.empty[String, Int]) shouldBe None
  }

  "minRecursion" should "return minimum value from the map" in {
    Collections.minRecursion(Map("a" -> 3, "b" -> 2, "c" -> 4)) shouldBe Some(("b", 2))
    Collections.minRecursion(Map.empty[String, Int]) shouldBe None
  }

  "scanLeft" should "return the running total for the sequence" in {
    Collections.scanLeft(0)(Seq(1, 2, 3, 4))(_ + _) shouldBe Seq(0, 1, 3, 6, 10)
  }

  "count" should "count the consistent occurrences of each character in the string" in {
    Collections.count("aaabbc") shouldBe List(('a', 3), ('b', 2), ('c', 1))
    Collections.count("") shouldBe List()
  }
}
