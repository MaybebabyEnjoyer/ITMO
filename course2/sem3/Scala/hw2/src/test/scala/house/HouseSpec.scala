package house

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HouseSpec extends AnyFlatSpec with Matchers {

  "House" should "return error for a house with less than 1 floor" in {
    House(Economy, 0, 1, 2, 3) shouldEqual Left("Floors must be greater than 0")
  }

  it should "return error for a house with less than 1 length" in {
    House(Economy, 1, 0, 2, 3) shouldEqual Left("Length must be greater than 0")
  }

  it should "return error for a house with less than 1 width" in {
    House(Economy, 1, 1, 0, 3) shouldEqual Left("Width must be greater than 0")
  }

  it should "return error for a house with less than 1 height" in {
    House(Economy, 1, 1, 2, 0) shouldEqual Left("Height must be greater than 0")
  }

  it should "return error for a house with less than 1 floor, length, width, and height" in {
    House(Economy, 0, 0, 0, 0) shouldEqual Left("Floors must be greater than 0")
  }

  it should "return error when trying to calculate floor cost for a house with less than 1 floor, lenth, width or height" in {
    House.floorCost(Economy, 0, 0, 0, 0) shouldEqual Left("Floors must be greater than 0")
  }

  it should "calculate floor cost for a premium house with less than 5 floors" in {
    val expected = BigInt(3).pow(3) * (1 + 2 + 3)
    House.floorCost(Premium, 3, 1, 2, 3) shouldEqual Right(expected)
  }

  it should "calculate floor cost for a premium house with 5 or more floors" in {
    val expected = BigInt(2).pow(5) * (1 + 2 + 3)
    House.floorCost(Premium, 5, 1, 2, 3) shouldEqual Right(expected)
  }

  it should "calculate floor cost for an economy house" in {
    val expected = 1 * 2 * 3 + 2 * 10000
    House.floorCost(Economy, 2, 1, 2, 3) shouldEqual Right(expected)
  }
}
