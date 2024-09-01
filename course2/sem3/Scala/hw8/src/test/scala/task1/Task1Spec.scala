package task1

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Task1Spec extends AnyFlatSpec with Matchers {
  "Functor" should "correctly map over Tree" in {
    val tree = Branch(Leaf(1), Leaf(2))
    val result = Tree.treeFunctor.map(tree)(_ * 2)

    result shouldBe Branch(Leaf(2), Leaf(4))
  }

  "Apply" should "correctly apply a function inside a Tree to another Tree" in {
    val treeFunc = Branch(Leaf((x: Int) => x * 2), Leaf((x: Int) => x + 3))
    val tree = Branch(Leaf(1), Leaf(2))
    val result = Tree.treeApply.ap(treeFunc)(tree)

    result shouldBe Branch(Leaf(2), Leaf(5))
  }

  "Applicative" should "correctly wrap a value into a Tree" in {
    val result = Tree.treeApplicative.pure(42)

    result shouldBe Leaf(42)
  }

  it should "correctly apply a function inside a Tree to another Tree" in {
    val treeFunc = Branch(Leaf((x: Int) => x * 2), Leaf((x: Int) => x + 3))
    val tree = Branch(Leaf(1), Leaf(2))
    val result = Tree.treeApplicative.ap(treeFunc)(tree)

    result shouldBe Branch(Leaf(2), Leaf(5))
  }

  "FlatMap" should "correctly flatMap a function over Tree" in {
    val tree = Branch(Leaf(1), Leaf(2))
    val result = Tree.treeFlatMap.flatMap(tree)(x => Branch(Leaf(x), Leaf(x * 2)))

    result shouldBe Branch(Branch(Leaf(1), Leaf(2)), Branch(Leaf(2), Leaf(4)))
  }

  "Monad" should "correctly flatMap a function over Tree" in {
    val tree = Branch(Leaf(1), Leaf(2))
    val result = Tree.treeMonad.flatMap(tree)(x => Branch(Leaf(x), Leaf(x * 2)))

    result shouldBe Branch(Branch(Leaf(1), Leaf(2)), Branch(Leaf(2), Leaf(4)))
  }

  it should "correctly wrap a value into a Tree using pure" in {
    val result = Tree.treeMonad.pure(42)

    result shouldBe Leaf(42)
  }
}
