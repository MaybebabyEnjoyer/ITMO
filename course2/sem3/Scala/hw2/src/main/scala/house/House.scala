package house

sealed trait HouseType
case object Premium extends HouseType
case object Economy extends HouseType

case class House private (houseType: HouseType, floors: Int, length: Int, width: Int, height: Int) {

  def floorCost: BigInt = houseType match {
    case Premium if floors < 5 => BigInt(3).pow(floors) * BigInt(length + width + height)
    case Premium               => BigInt(2).pow(floors) * BigInt(length + width + height)
    case Economy               => BigInt(length * width * height) + BigInt(floors * 10000)
  }
}

object House {

  def apply(houseType: HouseType, floors: Int, length: Int, width: Int, height: Int): Either[String, House] = {
    (floors, length, width, height) match {
      case (f, _, _, _) if f < 1 => Left("Floors must be greater than 0")
      case (_, l, _, _) if l < 1 => Left("Length must be greater than 0")
      case (_, _, w, _) if w < 1 => Left("Width must be greater than 0")
      case (_, _, _, h) if h < 1 => Left("Height must be greater than 0")
      case _                     => Right(new House(houseType, floors, length, width, height))
    }
  }

  def floorCost(houseType: HouseType, floors: Int, length: Int, width: Int, height: Int): Either[String, BigInt] = {
    House(houseType, floors, length, width, height) match {
      case Right(house) => Right(house.floorCost)
      case Left(error)  => Left(error)
    }
  }
}
