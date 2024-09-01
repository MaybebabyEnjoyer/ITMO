package io

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.util.{Failure, Success}

class IOSpec extends AnyFlatSpec with Matchers {
  // тут упадет если хотите воровать надо чинить
//  "attempt" should "not overflow the stack" in {
//    val deeplyNestedAttempt = (1 to 100000).foldLeft[IO[_]](IO(1))((acc, _) => acc.attempt)
//    deeplyNestedAttempt.unsafeRunSync() match {
//      case Right(_) => ()
//      case Left(_) => fail
//    }
//  }

  "IO" should "correctly handle map operations" in {
    val io = IO(1).map(_ + 1)
    io.unsafeRunSync() should be(2)
  }

  it should "correctly handle flatMap operations" in {
    val io = IO(1).flatMap(n => IO(n + 1))
    io.unsafeRunSync() should be(2)
  }

  it should "correctly handle attempt operations" in {
    val io = IO.delay(throw new RuntimeException("Error")).attempt
    io.unsafeRunSync().isLeft should be(true)
  }

  it should "correctly handle option operations" in {
    val io = IO(1).option
    io.unsafeRunSync() should be(Some(1))
  }

  it should "correctly handle handleErrorWith operations" in {
    val io = IO(throw new RuntimeException("Error")).handleErrorWith(_ => IO(1))
    io.unsafeRunSync() should be(1)
  }

  it should "correctly handle *> operations" in {
    val io = IO(1) *> IO(2)
    io.unsafeRunSync() should be(2)
  }

  it should "correctly handle as operations" in {
    val io = IO(1).as(2)
    io.unsafeRunSync() should be(2)
  }

  it should "correctly handle void operations" in {
    val io = IO(1).void
    io.unsafeRunSync() should be(())
  }

  it should "correctly handle redeem operations" in {
    val io = IO(throw new RuntimeException("Error")).redeem(_ => -1, identity[Int])
    io.unsafeRunSync() should be(-1)
  }

  it should "correctly handle redeemWith operations" in {
    val io = IO(throw new RuntimeException("Error")).redeemWith(_ => IO(-1), (a: Int) => IO(a))
    io.unsafeRunSync() should be(-1)
  }

  it should "correctly handle raiseError operations" in {
    val io = IO.raiseError[Int](new RuntimeException("Error"))
    an[RuntimeException] should be thrownBy io.unsafeRunSync()
  }

  it should "correctly handle raiseUnless operations" in {
    val io = IO.raiseUnless(cond = false)(new RuntimeException("Error"))
    an[RuntimeException] should be thrownBy io.unsafeRunSync()
  }

  it should "correctly handle raiseWhen operations" in {
    val io = IO.raiseWhen(cond = true)(new RuntimeException("Error"))
    an[RuntimeException] should be thrownBy io.unsafeRunSync()
  }

  it should "correctly handle unlessA operations" in {
    val io = IO.unlessA(cond = false)(IO(1))
    io.unsafeRunSync() should be(())
  }

  it should "correctly handle whenA operations" in {
    val io = IO.whenA(cond = true)(IO(1))
    io.unsafeRunSync() should be(())
  }

  "IO.fromEither" should "return the right value from a Right" in {
    val io = IO.fromEither(Right(42))
    io.unsafeRunSync() should be(42)
  }

  it should "throw an exception for a Left" in {
    val io = IO.fromEither(Left(new RuntimeException("Error")))
    an[RuntimeException] should be thrownBy io.unsafeRunSync()
  }

  "IO.fromOption" should "return the value from a Some" in {
    val io = IO.fromOption(Some(42))(new RuntimeException("No value"))
    io.unsafeRunSync() should be(42)
  }

  it should "throw an exception for a None" in {
    val io = IO.fromOption(None)(new RuntimeException("No value"))
    an[RuntimeException] should be thrownBy io.unsafeRunSync()
  }

  "IO.fromTry" should "return the value from a Success" in {
    val io = IO.fromTry(Success(42))
    io.unsafeRunSync() should be(42)
  }

  it should "throw an exception for a Failure" in {
    val io = IO.fromTry(Failure(new RuntimeException("Error")))
    an[RuntimeException] should be thrownBy io.unsafeRunSync()
  }

  "IO.none" should "return None" in {
    val io = IO.none[Int]
    io.unsafeRunSync() should be(None)
  }

  "IO.suspend" should "defer the execution of an IO operation" in {
    var executed = false
    val io = IO.suspend(IO {
      executed = true; 42
    })
    executed should be(false)
    io.unsafeRunSync()
    executed should be(true)
  }

  "Stack safety" should "not overflow the stack with a large sequence of flatMaps" in {
    val largeNumber = 1000000
    val io = (1 to largeNumber).foldLeft(IO(0))((acc, _) => acc.flatMap(n => IO(n + 1)))
    io.unsafeRunSync() mustBe largeNumber
  }
}
