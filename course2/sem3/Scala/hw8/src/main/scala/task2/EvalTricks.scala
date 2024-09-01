package task2

import cats.Eval

object EvalTricks {

  def fib(n: Int): Eval[BigInt] = {
    def loop(n: Int): Eval[BigInt] = n match {
      case 0 => Eval.now(0)
      case 1 => Eval.now(1)
      case _ =>
        Eval.defer(loop(n - 1)).flatMap(a => Eval.defer(loop(n - 2)).map(b => a + b))
    }

    loop(n)
  }

  def foldRight[A, B](as: List[A], acc: Eval[B])(fn: (A, Eval[B]) => Eval[B]): Eval[B] = as match {
    case head :: tail =>
      Eval.defer(fn(head, foldRight(tail, acc)(fn)))
    case Nil => acc
  }

}
