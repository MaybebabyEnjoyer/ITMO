package provetrivanie

object Provetrivanie {

  sealed case class InputError() {
    override def toString: String = "Ensure that n >= k and k > 0"
  }

  def maxInSublists(list: List[Int], windowSize: Int): Either[InputError, List[Int]] = list.size match {
    case 0                         => Right(List.empty)
    case size if windowSize <= 0   => Left(InputError())
    case size if windowSize > size => Left(InputError())
    case _                         => Right(list.sliding(windowSize).map(_.max).toList)
  }

  def maxInSublistsOptimized(list: List[Int], k: Int): Either[InputError, List[Int]] = list.size match {
    case 0                => Right(List.empty)
    case size if k <= 0   => Left(InputError())
    case size if k > size => Left(InputError())
    case _ =>
      def processElem(i: Int, dq: Vector[Int], acc: List[Int]): (Vector[Int], List[Int]) = {
        val newDq1 = dq.dropWhile(idx => idx <= i - k)
        val newDq2 = newDq1.reverse.dropWhile(idx => list(idx) <= list(i)).reverse
        val updatedDq = newDq2 :+ i

        i match {
          case idx if idx >= k - 1 => (updatedDq, list(updatedDq.head) :: acc)
          case _                   => (updatedDq, acc)
        }
      }

      val (_, result) = list.indices.foldLeft((Vector.empty[Int], List.empty[Int])) { case ((dq, acc), i) =>
        processElem(i, dq, acc)
      }

      Right(result.reverse)
  }
}
