import java.util
import scala.io.StdIn
import scala.collection
import java.util.Arrays
import scala.util.control.Breaks._

object Algo_4_B {
  def main(args: Array[String]): Unit = {
    var stack = new Array[Int](100000)
    var n = 100
    var head = 0
    var tail = 0
    while (true) {
      breakable {
        val input = StdIn.readLine().split(" ")
        if (input(0) == "push") {
          stack(tail) = input(1).toInt
          tail = tail + 1
          println("ok")
        }
        if (input(0) == "pop") {
          println(stack(head))
          head = head + 1
          break()
        }
        if (input(0) == "front") {
          println(stack(head))
          break()
        }
        if (input(0) == "size") {
          if (head > tail) println(n - head + tail)
          else println(tail - head)
          break()
        }
        if (input(0) == "clear") {
          println("ok")
          stack = new Array[Int](100000)
          tail = 0
          head = 0
          break()
        }
        if (input(0) == "exit") {
          println("bye")
          System.exit(0)
        }
      }
    }
  }
}