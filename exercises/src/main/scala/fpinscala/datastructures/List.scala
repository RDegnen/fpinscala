package fpinscala.datastructures

sealed trait List[+A] // `List` data type, parameterized on a type, `A`
case object Nil extends List[Nothing] // A `List` data constructor representing the empty list
/* Another data constructor, representing nonempty lists. Note that `tail` is another `List[A]`,
which may be `Nil` or another `Cons`.
 */
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List { // `List` companion object. Contains functions for creating and working with lists.
  def sum(ints: List[Int]): Int = ints match { // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x,xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)
  }

  def apply[A](as: A*): List[A] = // Variadic function syntax
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  val x = List(1,2,3,4,5) match {
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + sum(t)
    case _ => 101
  }

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match {
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))
    }

  def foldRight[A,B](as: List[A], z: B)(f: (A, B) => B): B = // Utility functions
    as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }

  def sum2(ns: List[Int]) =
    foldRight(ns, 0)((x,y) => x + y)

  def product2(ns: List[Double]) =
    foldRight(ns, 1.0)(_ * _) // `_ * _` is more concise notation for `(x,y) => x * y`; see sidebar


  def tail[A](l: List[A]): List[A] =
    l match {
      case Nil => sys.error("tail of empty list")
      case Cons(_,xs) => xs
    }

  def setHead[A](l: List[A], h: A): List[A] =
    l match {
      case Nil => sys.error("Empty list")
      case Cons(_,xs) => Cons(h,xs)
    }

  def drop[A](l: List[A], n: Int): List[A] =
    l match {
      case Nil => Nil
      case Cons(x,xs) if n == 3 => xs
      case Cons(x,xs) => drop(xs, n + 1)
    }

  def dropWhile[A](l: List[A], f: A => Boolean): List[A] =
    l match {
      case Nil => Nil
      case Cons(x,xs) if f(x) => dropWhile(xs, f)
      case _ => l
    }

  def init[A](l: List[A]): List[A] = l match {
    case Cons(x,xs) => Cons(x,init(xs))
    case Cons(x,Nil) => Nil
  }

  def length[A](l: List[A]): Int =
    foldRight(l, 0)((_,y) => y + 1)

  // My wrong but compiles foldleft
//  def badfoldLeft[A,B](l: List[A], z: B)(f: (B, A) => B): B = {
//    @annotation.tailrec
//    def go(n: Int): B =
//      if (n == length(l)) z
//      else if (n < length(l)) badfoldLeft(l, f(z, l)(f))
//      else go(n + 1)
//
//    go(0)
//  }

  // correct one
  @annotation.tailrec
  def foldLeft[A,B](l: List[A], z: B)(f: (B, A) => B): B =
    l match {
      case Nil => z
      case Cons(h,t) => foldLeft(t, f(z,h))(f)
    }

  def leftSum(ns: List[Int]) =
    foldLeft(ns, 0)((x,y) => x + y)

  def leftProduct(ns: List[Double]) =
    foldLeft(ns, 1.0)(_ * _)

  def leftLength[A](l: List[A]): Int =
    foldLeft(l, 0)((x,_) => x + 1)

  def reverse[A](l: List[A]): List[A] =
    foldLeft(l, List[A]())((x,y) => Cons(y,x))

  def appendFoldRight[A](l: List[A], a: A): List[A] =
    foldRight(l, Cons(a,Nil))((x,y) => Cons(x,y))

  def concatDeepList[A](l: List[List[A]]): List[A] =
    foldRight(l, Nil:List[A])(append)

  def addOne(l: List[Int]): List[Int] =
    foldRight(l, Nil:List[Int])((x,y) => Cons(x+1,y))

  def dToString(l: List[Double]): List[String] =
    foldRight(l, Nil:List[String])((x,y) => Cons(x.toString, y))

  def map[A,B](l: List[A])(f: A => B): List[B] =
    foldRight(l, Nil:List[B])((x, y) => Cons(f(x), y))

  def filter[A](as: List[A])(f: A => Boolean): List[A] =
    foldRight(as, Nil:List[A])((h, t) => if(f(h)) Cons(h,t) else t)

  def flatMap[A,B](as: List[A])(f: A => List[B]): List[B] =
    foldRight(as, Nil:List[B])((h,t) => f(h)) // Fix this

  def filterViaFlatMap[A](l: List[A])(f: A => Boolean): List[A] =
    flatMap(l)(a => if (f(a)) List(a) else Nil)

  def addPairwise(a: List[Int], b: List[Int]): List[Int] = (a,b) match {
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (Cons(h1,t1), Cons(h2,t2)) => Cons(h1+h2, addPairwise(t1,t2))
  }

  def zipWith[A,B,C](a: List[A], b: List[B])(f: (A,B) => C): List[C] = (a,b) match {
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (Cons(h1,t1), Cons(h2,t2)) => Cons(f(h1,h2), zipWith(t1,t2)(f))
  }

  @annotation.tailrec
  def startsWith[A](l: List[A], prefix: List[A]): Boolean = (l,prefix) match {
    case (_,Nil) => true
    case (Cons(h,t),Cons(h2,t2)) if h == h2 => startsWith(t, t2)
    case _ => false
  }
  @annotation.tailrec
  def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean = sup match {
    case Nil => sub == Nil
    case _ if startsWith(sup, sub) => true
    case Cons(_,t) => hasSubsequence(t, sub)
  }

  def main(args: Array[String]): Unit = {
    println(flatMap(List(1,2,3))(i => List(i,i)))
  }
}
