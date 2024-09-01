package collections

import scala.annotation.tailrec

object Collections {

  /** In a sorted list find all pairs of two neighbor numbers which have a gap between them None for Seq(1, 2, 3, 4)
    * Some(Seq((2, 8))) for Seq(1, 2, 8) Some(Seq((3, 5), (5, 7))) for Seq(3, 5, 7)
    */
  def findGaps(l: Seq[Int]): Option[Seq[(Int, Int)]] = {
    val pairs = l zip l.tail
    val gaps = pairs.collect { case (a, b) if b - a > 1 => (a, b) }
    if (gaps.isEmpty) None else Some(gaps)
  }

  /** Find key-value pair with the minimum value in the map try to implement min in different ways (fold, reduce,
    * recursion)
    */
  def minFold(map: Map[String, Int]): Option[(String, Int)] = {
    map.foldLeft(Option.empty[(String, Int)]) {
      case (None, (k, v))                       => Some((k, v))
      case (Some((_, v1)), (k2, v2)) if v2 < v1 => Some((k2, v2))
      case (acc, _)                             => acc
    }
  }

  def minReduce(map: Map[String, Int]): Option[(String, Int)] = {
    if (map.isEmpty) None
    else Some(map.reduce((a, b) => if (a._2 < b._2) a else b))
  }

  def minRecursion(map: Map[String, Int]): Option[(String, Int)] = {
    @tailrec
    def minR(entries: Seq[(String, Int)], currentMin: (String, Int)): (String, Int) = {
      entries match {
        case Nil => currentMin
        case head :: tail =>
          val newMin = if (head._2 < currentMin._2) head else currentMin
          minR(tail, newMin)
      }
    }

    if (map.isEmpty) None
    else Some(minR(map.tail.toSeq, map.head))
  }

  /** Implement scanLeft - running total, applying [f] to elements of [list] (not using scans ofc) */
  def scanLeft[T](zero: T)(list: Seq[T])(f: (T, T) => T): Seq[T] = {
    list.foldLeft(Seq(zero))((acc, x) => acc :+ f(acc.last, x))
  }

  /** Count the consistent occurrences of each character in the string */
  def count(s: String): List[(Char, Int)] = {
    s.foldLeft(List.empty[(Char, Int)]) {
      case ((char, count) :: tail, c) if char == c => (char, count + 1) :: tail
      case (acc, c)                                => (c, 1) :: acc
    }.reverse
  }
}
