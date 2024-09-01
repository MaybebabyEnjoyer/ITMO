package io

import scala.util.{Try, Success, Failure}

import scala.annotation.tailrec

case class Pure[A](a: A) extends IO[A]
case class FlatMap[A, B](sub: IO[A], k: A => IO[B]) extends IO[B]

sealed trait IO[+A] { self =>
  protected def run(): A = IO.run(self)
  def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(self, f)
  def map[B](f: A => B): IO[B] = flatMap(a => IO.pure(f(a)))

  def *>[B](another: IO[B]): IO[B] = self.flatMap(_ => another)

  def as[B](newValue: => B): IO[B] = self.map(_ => newValue)

  def void: IO[Unit] = self.map(_ => ())

  def attempt: IO[Either[Throwable, A]] = IO {
    Try(self.run()).toEither
  }
  def option: IO[Option[A]] = self.attempt.map(_.toOption)

  def handleErrorWith[AA >: A](f: Throwable => IO[AA]): IO[AA] = IO {
    Try(self.run()).recoverWith { case e: Throwable =>
      Try(f(e).run())
    }.get
  }

  def redeem[B](recover: Throwable => B, map: A => B): IO[B] = IO {
    Try(self.run()).fold(e => recover(e), a => map(a))
  }

  def redeemWith[B](recover: Throwable => IO[B], bind: A => IO[B]): IO[B] = IO {
    Try(self.run()).fold(e => recover(e).run(), a => bind(a).run())
  }
  def unsafeRunSync(): A = run()
}

object IO {
  def apply[A](body: => A): IO[A] = new IO[A] {
    override protected def run(): A = body
  }
  def suspend[A](thunk: => IO[A]): IO[A] = thunk
  def delay[A](body: => A): IO[A] = apply(body)
  def pure[A](a: A): IO[A] = Pure(a)

  def fromEither[A](e: Either[Throwable, A]): IO[A] = e match {
    case Right(a)  => pure(a)
    case Left(err) => raiseError(err)
  }
  def fromOption[A](option: Option[A])(orElse: => Throwable): IO[A] = option match {
    case Some(a) => pure(a)
    case None    => raiseError(orElse)
  }

  def fromTry[A](t: Try[A]): IO[A] = t match {
    case Success(a)   => pure(a)
    case Failure(err) => raiseError(err)
  }
  def none[A]: IO[Option[A]] = pure(None)

  def raiseError[A](e: Throwable): IO[A] = new IO[A] {
    override protected def run(): A = throw e
  }

  def raiseUnless(cond: Boolean)(e: => Throwable): IO[Unit] = if (!cond) raiseError(e) else unit

  def raiseWhen(cond: Boolean)(e: => Throwable): IO[Unit] = if (cond) raiseError(e) else unit

  def unlessA(cond: Boolean)(action: => IO[Unit]): IO[Unit] = if (!cond) action else unit

  def whenA(cond: Boolean)(action: => IO[Unit]): IO[Unit] = if (cond) action else unit
  private val unit: IO[Unit] = pure(())

  @tailrec
  private def run[A](io: IO[A]): A = io match {
    case Pure(a) => a
    case FlatMap(sub, k) =>
      sub match {
        case Pure(a)           => run(k(a))
        case FlatMap(sub2, k2) => run(sub2.flatMap(a => k2(a).flatMap(k)))
        case _                 => run(k(sub.run()))
      }
    case _ => io.run()
  }
}
