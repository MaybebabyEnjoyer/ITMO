package fibonacci

object Fibonacci {
  def fibonacci(limit: Long): BigInt =
    (BigInt(1) to BigInt(limit)).foldLeft(BigInt(0), BigInt(1))((a, _) => (a._2, a._1 + a._2))._1
}
