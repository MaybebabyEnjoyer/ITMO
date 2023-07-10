import scala.io.StdIn
import scala.collection
import java.util
import scala.util.control.Breaks._

object Algo_A_4 {
  def main(args: Array[String]): Unit = {
    val stack = new util.ArrayList[Int]()
    while (true) {
      breakable {
        val input = StdIn.readLine().split(" ")
        if (input(0) == "push") {
          stack.add(input(1).toInt)
          println("ok")
          break()
        }
        if (input(0) == "pop") {
          println(stack.get(stack.size() - 1))
          stack.remove(stack.size() - 1)
          break()
        }
        if (input(0) == "back") {
          println(stack.get(stack.size() - 1))
          break()
        }
        if (input(0) == "size") {
          println(stack.size())
          break()
        }
        if (input(0) == "clear") {
          println("ok")
          stack.clear()
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