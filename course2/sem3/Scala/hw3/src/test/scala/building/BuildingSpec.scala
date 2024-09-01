package building

import building.Building.{countCommercial, countCommercialAvg, countOldManFloors, evenFloorsMenAvg, womanMaxAge}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BuildingSpec extends AnyFlatSpec with Matchers {

  val floor3: Commercial = Commercial(List("Cafe"), Some(CommercialAttic("Cafe")))
  val floor2: Residential = Residential(List(Person(45, Male), Person(24, Female)), Some(floor3))
  val floor1: Residential = Residential(List(Person(28, Male), Person(30, Female)), Some(floor2))

  "countOldManFloors" should "return the number of men older than the specified age" in {
    Building("Some Street 123", floor1) match {
      case Right(building) =>
        countOldManFloors(building, 40) shouldEqual 1
        countOldManFloors(building, 20) shouldEqual 2
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }

  it should "return 0 if there are no men older than the specified age" in {
    Building("Some Street 123", floor1) match {
      case Right(building) =>
        countOldManFloors(building, 50) shouldEqual 0
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }

  it should "return 0 if there are no men at all in the building" in {
    val floorWithoutMen = Residential(List(Person(28, Female), Person(30, Female)), Some(CommercialAttic("Cafe")))
    Building("No Men Street 123", floorWithoutMen) match {
      case Right(building) =>
        countOldManFloors(building, 40) shouldEqual 0
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }

  "womanMaxAge" should "find age of the oldest woman in the building" in {
    Building("Some Street 123", floor1) match {
      case Right(building) =>
        womanMaxAge(building) shouldEqual 30
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }

  it should "return 0 if there are no women in the building" in {
    val floorWithoutWomen = Residential(List(Person(28, Male), Person(30, Male)), Some(CommercialAttic("Cafe")))
    Building("No Women Street 123", floorWithoutWomen) match {
      case Right(building) =>
        womanMaxAge(building) shouldEqual 0
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }

  "countCommercial" should "return number of commercial establishments in the building" in {
    Building("Some Street 123", floor1) match {
      case Right(building) =>
        countCommercial(building) shouldEqual 2
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }

  it should "return 0 if there are no commercial establishments in the building" in {
    val floorWithoutCommercial = Residential(List(Person(28, Male), Person(30, Female)), Some(RegularAttic))
    Building("No Commercial Street 123", floorWithoutCommercial) match {
      case Right(building) =>
        countCommercial(building) shouldEqual 0
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }
  "countCommercialAvg" should "return the average number of commercial establishments in buildings" in {
    val floor1 = Commercial(List("Cafe", "Shop"), Some(CommercialAttic("Bar")))
    val floor2 = Commercial(List("Restaurant"), Some(CommercialAttic("Lounge")))
    val building1Either = Building("Street 123", floor1)
    val building2Either = Building("Street 456", floor2)

    (building1Either, building2Either) match {
      case (Right(building1), Right(building2)) =>
        countCommercialAvg(Array(building1, building2)) shouldEqual 2.5
      case (Left(error1), _) =>
        fail(s"Building 1 creation failed with error: $error1")
      case (_, Left(error2)) =>
        fail(s"Building 2 creation failed with error: $error2")
    }
  }

  it should "return 0 if there are no commercial establishments in the buildings" in {
    val floor1 = Residential(List(Person(28, Male), Person(30, Female)), Some(CommercialAttic("Bar")))
    val building1 = Building("Street 789", floor1)

    building1 match {
      case Right(building) =>
        countCommercialAvg(Array(building)) shouldEqual 1.0
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }

  "evenFloorsMenAvg" should "return the average number of men on even floors" in {
    val floor6 = CommercialAttic("Bar")
    val floor5 = Residential(List(Person(28, Male), Person(30, Male)), Some(floor6))
    val floor4 = Residential(List(Person(45, Male)), Some(floor5))
    val floor3 = Commercial(List("Cafe"), Some(floor4))
    val floor2 = Residential(List(Person(28, Male), Person(30, Male)), Some(floor3))
    val floor1 = Residential(List(Person(24, Female)), Some(floor2))

    Building("Street 123", floor1) match {
      case Right(building) =>
        evenFloorsMenAvg(building) shouldEqual 1.0
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }

  it should "return 0 if there are no men on even floors" in {
    val floor4 = Residential(List(Person(45, Female)), Some(CommercialAttic("Bar")))
    val floor3 = Commercial(List("Cafe"), Some(floor4))
    val floor2 = Residential(List(Person(24, Female)), Some(floor3))
    val floor1 = Residential(List(Person(28, Male)), Some(floor2))

    Building("Street 123", floor1) match {
      case Right(building) =>
        evenFloorsMenAvg(building) shouldEqual 0.0
      case Left(error) =>
        fail(s"Building creation failed with error: $error")
    }
  }

  "Building apply" should "return InvalidAge when there's a person with age less than or equal to 0" in {
    val floor = Residential(List(Person(-5, Male)), Some(CommercialAttic("Cafe")))
    Building("Some Street 123", floor) shouldEqual Left(InvalidAge)
  }

  it should "return InvalidCommercialFloor when there are no establishments in a commercial floor" in {
    val floor = Commercial(List.empty, Some(CommercialAttic("Cafe")))
    Building("Some Commercial Street 123", floor) shouldEqual Left(InvalidCommercialFloor)
  }

  it should "return BuildingNotEndingWithAttic when the last floor is not an attic" in {
    val floor3 = Residential(List(Person(45, Male), Person(24, Female)), None)
    val floor2 = Residential(List(Person(28, Male), Person(30, Female)), Some(floor3))
    val floor1 = Commercial(List("Cafe"), Some(floor2))
    Building("Some Street 123", floor1) shouldEqual Left(BuildingNotEndingWithAttic)
  }

  it should "successfully create a building when all conditions are met" in {
    val floor3 = Commercial(List("Cafe"), Some(CommercialAttic("Cafe")))
    val floor2 = Residential(List(Person(45, Male), Person(24, Female)), Some(floor3))
    val floor1 = Residential(List(Person(28, Male), Person(30, Female)), Some(floor2))
    Building("Some Street 123", floor1).isRight shouldBe true
  }
}
