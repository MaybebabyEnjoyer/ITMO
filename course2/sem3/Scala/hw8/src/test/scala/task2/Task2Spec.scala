package task2

import cats.Eval
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Task2Spec extends AnyFlatSpec with Matchers {
  "fib" should "compute the nth Fibonacci number without blowing up the stack" in {
    val n = 1000000
    noException should be thrownBy EvalTricks.fib(n)
  }

  "foldRight" should "fold a list without blowing up the stack" in {
    val largeList = (1 to 1000000).toList
    noException should be thrownBy EvalTricks.foldRight(largeList, Eval.now(0))((a, b) => b.map(_ + a)).value
  }
}
