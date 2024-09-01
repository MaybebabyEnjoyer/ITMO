package task1

import task1.hierarchy._

sealed trait Tree[+T]

case class Branch[T](left: Tree[T], right: Tree[T]) extends Tree[T]

case class Leaf[+T](value: T) extends Tree[T]

object Tree {

  implicit val treeFunctor: Functor[Tree] = new Functor[Tree] {
    def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
      case Leaf(value) => Leaf(f(value))
      case Branch(left, right) => Branch(map(left)(f), map(right)(f))
    }
  }

  implicit val treeApply: Apply[Tree] = new Apply[Tree] {
    def ap[A, B](ff: Tree[A => B])(fa: Tree[A]): Tree[B] = (ff, fa) match {
      case (Leaf(f), Leaf(a)) => Leaf(f(a))
      case (Leaf(f), Branch(left, right)) => Branch(map(left)(f), map(right)(f))
      case (Branch(leftF, rightF), Leaf(a)) => Branch(ap(leftF)(Leaf(a)), ap(rightF)(Leaf(a)))
      case (Branch(leftF, rightF), Branch(leftA, rightA)) => Branch(ap(leftF)(leftA), ap(rightF)(rightA))
      case (_, Branch(left, right)) => Branch(ap(ff)(left), ap(ff)(right))
      case (Branch(left, right), _) => Branch(ap(left)(fa), ap(right)(fa))
    }

    def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = treeFunctor.map(fa)(f)
  }

  implicit val treeApplicative: Applicative[Tree] = new Applicative[Tree] {
    def pure[A](a: A): Tree[A] = Leaf(a)

    def ap[A, B](ff: Tree[A => B])(fa: Tree[A]): Tree[B] = treeApply.ap(ff)(fa)

    def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = treeFunctor.map(fa)(f)
  }

  implicit val treeFlatMap: FlatMap[Tree] = new FlatMap[Tree] {
    def flatMap[A, B](fa: Tree[A])(f: A => Tree[B]): Tree[B] = fa match {
      case Leaf(value) => f(value)
      case Branch(left, right) => Branch(flatMap(left)(f), flatMap(right)(f))
    }

    def ap[A, B](ff: Tree[A => B])(fa: Tree[A]): Tree[B] = treeApply.ap(ff)(fa)

    def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = treeFunctor.map(fa)(f)
  }

  implicit val treeMonad: Monad[Tree] = new Monad[Tree] {
    def pure[A](a: A): Tree[A] = treeApplicative.pure(a)

    def flatMap[A, B](fa: Tree[A])(f: A => Tree[B]): Tree[B] = treeFlatMap.flatMap(fa)(f)

    def ap[A, B](ff: Tree[A => B])(fa: Tree[A]): Tree[B] = treeApply.ap(ff)(fa)

    def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = treeFunctor.map(fa)(f)
  }
}
