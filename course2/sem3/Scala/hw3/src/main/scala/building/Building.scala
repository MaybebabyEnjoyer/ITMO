package building
import scala.annotation.tailrec

/** Здание должно иметь:
  *   - строковый адрес
  *   - этажи (сходящиеся к первому этажу) Этаж может быть жилым, коммерческим, либо чердаком (который сам может быть
  *     коммерческим). На каждом жилом этаже живет 2 человека и есть лестница(ссылка) ведущая на следующий этаж У
  *     каждого человека есть возраст (>0) и пол На коммерческом этаже может быть несколько заведений (используйте
  *     Array), но не меньше 1. Здание всегда должно заканчиваться чердаком На чердаке никто не живет, но это может быть
  *     и коммерческое помещение (но только 1).
  */

sealed trait ValidationError
object InvalidAge extends ValidationError
object InvalidCommercialFloor extends ValidationError
object BuildingNotEndingWithAttic extends ValidationError

sealed trait Gender
case object Male extends Gender
case object Female extends Gender

final case class Person(age: Int, gender: Gender)

sealed trait Floor {
  def next: Option[Floor]
}

case class Commercial(establishments: List[String], next: Option[Floor]) extends Floor

case class Residential(residents: List[Person], next: Option[Floor]) extends Floor

sealed trait Attic extends Floor {
  def next: Option[Floor] = None
}

case class CommercialAttic(establishment: String) extends Attic

case object RegularAttic extends Attic

case class Building private (address: String, firstFloor: Floor)

object Building {

  def apply(address: String, firstFloor: Floor): Either[ValidationError, Building] = {
    def validateFloor(floor: Floor): Either[ValidationError, Floor] = floor match {
      case Residential(residents, next) =>
        val validResidents = residents
          .find(r => r.age <= 0)
          .map(_ => InvalidAge)
          .toLeft(())

        val nextFloorValidation = next.map(validateFloor).getOrElse(Right(()))

        for {
          _ <- validResidents
          _ <- nextFloorValidation
        } yield floor

      case Commercial(establishments, _) if establishments.isEmpty => Left(InvalidCommercialFloor)
      case com: Commercial                                         => validateFloor(com.next.getOrElse(RegularAttic))
      case _                                                       => Right(floor)
    }

    @tailrec
    def isLastFloorAttic(floor: Floor): Boolean = floor match {
      case _: Attic => true
      case floorWithNext: Floor =>
        floorWithNext.next match {
          case Some(nextFloor) => isLastFloorAttic(nextFloor)
          case None            => false
        }
    }

    if (isLastFloorAttic(firstFloor)) {
      validateFloor(firstFloor).map(_ => new Building(address, firstFloor))
    } else {
      Left(BuildingNotEndingWithAttic)
    }
  }

  /** Проходится по зданию снизу вверх, применяя функцию [[f]] на каждом этаже с начальным аккумулятором [[accumulator]]
    */
  private def fold[A](building: Building, accumulator: A)(f: (A, Floor) => A): A = {
    @tailrec
    def loop(floor: Option[Floor], acc: A): A = floor match {
      case Some(res: Residential) => loop(res.next, f(acc, res))
      case Some(comm: Commercial) => loop(comm.next, f(acc, comm))
      case Some(attic: Attic)     => loop(attic.next, f(acc, attic))
      case None                   => acc
    }

    loop(Some(building.firstFloor), accumulator)
  }

  /** Подсчитывает количество этаже, на которых живет хотя бы один мужчина старше [[olderThan]]. Используйте [[fold]] */
  def countOldManFloors(building: Building, olderThan: Int): Int = fold(building, 0) { (acc, floor) =>
    floor match {
      case res: Residential if res.residents.exists(p => p.gender == Male && p.age > olderThan) => acc + 1
      case _                                                                                    => acc
    }
  }

  /** Находит наибольший возраст женьщины, проживающей в здании. Используйте [[fold]] */
  def womanMaxAge(building: Building): Int = fold(building, 0) { (maxAge, floor) =>
    floor match {
      case res: Residential =>
        val maxWomanAgeOnFloor = res.residents.filter(_.gender == Female).map(_.age).maxOption.getOrElse(0)
        Math.max(maxAge, maxWomanAgeOnFloor)
      case _ => maxAge
    }
  }

  /** Находит кол-во коммерческих заведений в здании. Используйте [[fold]] */
  def countCommercial(building: Building): Int = fold(building, 0) { (acc, floor) =>
    floor match {
      case com: Commercial    => acc + com.establishments.length
      case _: CommercialAttic => acc + 1
      case _                  => acc
    }
  }

  /** Находит среднее кол-во коммерческих заведений зданиях. Реализуйте свою функцию, похожую на [[fold]] для прохода по
    * зданию
    */
  def countCommercialAvg(buildings: Array[Building]): Double = {
    val (totalEstablishments, totalCount) = buildings.foldLeft((0, 0)) { (acc, building) =>
      val (sum, count) = acc
      (sum + countCommercial(building), count + 1)
    }

    totalEstablishments.toDouble / totalCount
  }

  /** Находит среднее кол-во мужчин на четных этажах. Реализуйте свою функцию, похожую на [[fold]] для прохода по зданию
    */
  def evenFloorsMenAvg(building: Building): Double = {
    val (totalMen, evenFloors, _) = fold(building, (0, 0, 1)) { (acc, floor) =>
      val (sum, evenCount, currentFloor) = acc

      (currentFloor % 2, floor) match {
        case (0, Residential(residents, _)) =>
          (sum + residents.count(_.gender == Male), evenCount + 1, currentFloor + 1)
        case (0, _) =>
          (sum, evenCount + 1, currentFloor + 1)
        case _ =>
          (sum, evenCount, currentFloor + 1)
      }
    }

    if (evenFloors > 0) totalMen.toDouble / evenFloors else 0.0
  }
}
