package tree

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TreeSpec extends AnyFlatSpec with Matchers {

  "smart constructor" should "create a tree from list of values" in {
    val tree = Tree(1, 2, 3).toOption.get
    Tree.print(tree) shouldEqual "1\n2\n3"
  }

  it should "return an error if list is empty" in {
    Tree() shouldEqual Left(TreeException())
  }

  "add" should "add values to the end of tree" in {
    val tree = Tree(1, 2, 4).toOption.get
    val newTree = Tree.add(tree, 3)
    Tree.print(newTree) shouldEqual "1\n2\n4\n3"
  }

  "delete" should "remove the specified value from tree" in {
    val tree = Tree(1, 2, 3, 4).toOption.get
    Tree.print(Tree.delete(tree, 3)) shouldEqual "1\n2\n4"
    Tree.print(Tree.delete(tree, 1)) shouldEqual "2\n3\n4"
  }

  "breadthFirstSearch" should "return values in breadth first order" in {
    val tree = Tree(1, 2, 3, 4).toOption.get
    Tree.breadthFirstSearch(tree) shouldEqual List(1, 2, 3, 4)
  }

  "depthFirstSearch" should "return values in depth first (inorder) order" in {
    val tree = Tree(1, 2, 3, 4).toOption.get
    Tree.depthFirstSearch(tree) shouldEqual List(1, 2, 3, 4)
  }

  "max" should "return the maximum value in tree" in {
    val tree = Tree(1, 3, 2, 4).toOption.get
    Tree.max(tree, Tree.depthFirstSearch) shouldEqual 4
  }

  it should "return equal values for different traversals" in {
    val tree = Tree(1, 3, 2, 4).toOption.get
    Tree.max(tree, Tree.depthFirstSearch) shouldEqual Tree.max(tree, Tree.breadthFirstSearch)
  }

  "min" should "return the minimum value in tree" in {
    val tree = Tree(4, 2, 3, 1).toOption.get
    Tree.min(tree, Tree.depthFirstSearch) shouldEqual 1
  }

  it should "return equal values for different traversals" in {
    val tree = Tree(4, 2, 3, 1).toOption.get
    Tree.min(tree, Tree.depthFirstSearch) shouldEqual Tree.min(tree, Tree.breadthFirstSearch)
  }

  "size" should "return the correct size of tree" in {
    Tree.size(Tree(1, 2, 3).toOption.get) shouldEqual 3
  }
}
