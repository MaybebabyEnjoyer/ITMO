package tree
import scala.annotation.tailrec

sealed trait Tree
case object Empty extends Tree
case class Node(value: Int, left: Tree) extends Tree

sealed case class TreeException() {
  override def toString: String = "Tree can't be empty"
}

object Tree {
  def apply(values: Int*): Either[TreeException, Tree] = {
    if (values.isEmpty) Left(TreeException())
    else Right(values.foldLeft[Tree](Empty) { (tree, v) => add(tree, v) })
  }

  def add(tree: Tree, value: Int): Tree = tree match {
    case Empty         => Node(value, Empty)
    case Node(v, left) => Node(v, add(left, value))
  }

  def foldLeft[A](tree: Tree, acc: A)(f: (A, Int) => A): A = {
    @tailrec
    def loop(current: Tree, acc: A): A = current match {
      case Empty         => acc
      case Node(v, left) => loop(left, f(acc, v))
    }

    loop(tree, acc)
  }

  def delete(tree: Tree, value: Int): Tree = tree match {
    case Empty => Empty
    case Node(v, left) =>
      if (v == value) left
      else Node(v, delete(left, value))
  }

  def breadthFirstSearch(tree: Tree): List[Int] = foldLeft(tree, List.empty[Int]) { (acc, v) => acc :+ v }
  def depthFirstSearch(tree: Tree): List[Int] = breadthFirstSearch(tree)

  def max(tree: Tree, traversal: Tree => List[Int]): Int = traversal(tree).maxOption.getOrElse(-1)
  def min(tree: Tree, traversal: Tree => List[Int]): Int = traversal(tree).minOption.getOrElse(-1)

  def size(tree: Tree): Int = foldLeft(tree, 0) { (acc, _) => acc + 1 }

  def print(tree: Tree): String = {
    foldLeft(tree, List.empty[Int]) { (acc, v) => acc :+ v }.mkString("\n")
  }

}

// =========================== Попытка в RBT ===========================

//import tree.Tree.DepthFirst
//
//import scala.math.Ordering.Implicits._
//
//object Tree {
//  sealed trait Traversal
//  case object DepthFirst extends Traversal
//  case object BreadthFirst extends Traversal
//
//  sealed abstract class Tree[T: Ordering]
//  case class Node[T: Ordering](color: Color, left: Tree[T], a: T, right: Tree[T]) extends Tree[T]
//  case class Empty[T: Ordering]() extends Tree[T]
//
//  sealed trait Color
//  case object R extends Color
//  case object B extends Color
//
//  private def getSibling[T: Ordering](node: Tree[T], parent: Node[T]): Tree[T] = {
//    if (parent.left == node) parent.right else parent.left
//  }
//
//  final def insert[T: Ordering](e: T, tree: Tree[T]): Tree[T] = {
//
//    def build(color: Color, left: Tree[T], e: T, right: Tree[T]): Tree[T] = {
//      (color, left, e, right) match {
//        case (B, Node(R, Node(R, a, x, b), y, c), z, d) => Node(R, Node(B, a, x, b), y, Node(B, c, z, d))
//        case (B, Node(R, a, x, Node(R, b, y, c)), z, d) => Node(R, Node(B, a, x, b), y, Node(B, c, z, d))
//        case (B, a, x, Node(R, Node(R, b, y, c), z, d)) => Node(R, Node(B, a, x, b), y, Node(B, c, z, d))
//        case (B, a, x, Node(R, b, y, Node(R, c, z, d))) => Node(R, Node(B, a, x, b), y, Node(B, c, z, d))
//        case (c, l, x, r)                               => Node(c, l, x, r)
//        case _                                          => throw new NoSuchElementException
//      }
//    }
//
//    def ins(tree: Tree[T]): Tree[T] = {
//      tree match {
//        case Node(col, l, a, r) =>
//          if (e < a) build(col, ins(l), a, r)
//          else if (e > a) build(col, l, a, ins(r))
//          else tree
//        case Empty() => Node(R, Empty(), e, Empty())
//      }
//    }
//
//    ins(tree) match {
//      case Node(_, l, v, r) => Node(B, l, v, r)
//    }
//  }
//
//  def foldLeft[T: Ordering, B](tree: Tree[T], acc: B, traversal: Traversal = DepthFirst)(f: (B, T) => B): B = {
//    @tailrec
//    def loop(current: List[Tree[T]], acc: B): B = {
//      current match {
//        case Nil             => acc
//        case Empty() :: tail => loop(tail, acc)
//        case Node(_, left, value, right) :: tail =>
//          traversal match {
//            case DepthFirst   => loop(left :: right :: tail, f(acc, value))
//            case BreadthFirst => loop(tail :+ left :+ right, f(acc, value))
//          }
//      }
//    }
//
//    loop(List(tree), acc)
//  }
//
//  def size[T: Ordering](tree: Tree[T]): Int = {
//    foldLeft(tree, 0)((acc, _) => acc + 1)
//  }
//
//  def dfs[T: Ordering](tree: Tree[T]): List[T] = {
//    foldLeft(tree, List.empty[T]) { (acc, value) =>
//      acc :+ value
//    }
//  }
//
//  def bfs[T: Ordering](tree: Tree[T]): List[T] = {
//    foldLeft(tree, List.empty[T], BreadthFirst) { (acc, value) =>
//      acc :+ value
//    }
//  }
//
//  def max[T: Ordering](tree: Tree[T], traversalMethod: Traversal): T = traversalMethod match {
//    case DepthFirst   => bfs(tree).max
//    case BreadthFirst => dfs(tree).max
//  }
//
//  def min[T: Ordering](tree: Tree[T], traversalMethod: Traversal): T = traversalMethod match {
//    case DepthFirst   => bfs(tree).min
//    case BreadthFirst => dfs(tree).min
//  }
//
//  def bfsWithLevels[T: Ordering](tree: Tree[T]): List[(Option[T], Int)] = {
//    @tailrec
//    def loop(remaining: List[(Tree[T], Int)], acc: List[(Option[T], Int)]): List[(Option[T], Int)] = {
//      remaining match {
//        case Nil                      => acc.reverse
//        case (Empty(), level) :: tail => loop(tail, (None, level) :: acc)
//        case (Node(_, left, value, right), level) :: tail =>
//          loop(tail :+ (left, level + 1) :+ (right, level + 1), (Some(value), level) :: acc)
//      }
//    }
//
//    loop(List((tree, 0)), List())
//  }
//
//  def printTree[T: Ordering](tree: Tree[T]): String = {
//    val levels = bfsWithLevels(tree)
//    val maxLevel = levels.map(_._2).max
//    (0 to maxLevel)
//      .map { level =>
//        levels
//          .filter(_._2 == level)
//          .map {
//            case (Some(value), _) => value.toString
//            case (None, _)        => "-"
//          }
//          .mkString(" ")
//      }
//      .mkString("\n")
//  }
//
//  def fromList[T: Ordering](values: List[T]): Tree[T] = {
//    values.foldLeft[Tree[T]](Empty())((tree, value) => insert(value, tree))
//  }
//
//  def getEmptyTree[T: Ordering]: Empty[T] = Empty[T]()
//
//  implicit class utils[T: Ordering](val tree: Tree[T]) {
//    def addValue(v: T): Tree[T] = insert(v, tree)
//  }
//
//  def rotateLeft[T: Ordering](node: Node[T]): Node[T] = node match {
//    case Node(color, left, value, Node(rcolor, rleft, rvalue, rright)) =>
//      Node(rcolor, Node(color, left, value, rleft), rvalue, rright)
//    case _ => throw new IllegalArgumentException("Left rotation can't be performed")
//  }
//
//  def rotateRight[T: Ordering](node: Node[T]): Node[T] = node match {
//    case Node(color, Node(lcolor, lleft, lvalue, lright), value, right) =>
//      Node(lcolor, lleft, lvalue, Node(color, lright, value, right))
//    case _ => throw new IllegalArgumentException("Right rotation can't be performed")
//  }
//
//  @tailrec
//  private def minNode[T: Ordering](tree: Tree[T]): Tree[T] = tree match {
//    case node @ Node(_, Empty(), _, _) => node
//    case Node(_, left, _, _)           => minNode(left)
//    case Empty()                       => throw new NoSuchElementException("Tree is empty")
//  }
//
//  def delete[T: Ordering](value: T, tree: Tree[T]): Tree[T] = {
//
//    def deleteInternal(current: Tree[T]): Tree[T] = current match {
//      case Empty() => Empty()
//
//      case Node(color, left, v, right) if value < v =>
//        Node(color, deleteInternal(left), v, right)
//
//      case Node(color, left, v, right) if value > v =>
//        Node(color, left, v, deleteInternal(right))
//
//      case Node(_, left, v, right) if v == value =>
//        (left, right) match {
//          case (Empty(), _) => right
//          case (_, Empty()) => left
//          case _ =>
//            val successor = minNode(right).asInstanceOf[Node[T]]
//            Node(successor.color, left, successor.a, deleteInternal(successor))
//        }
//    }
//
//    deleteInternal(tree) match {
//      case n: Node[T] => Node(B, n.left, n.a, n.right)
//      case e          => e
//    }
//  }
