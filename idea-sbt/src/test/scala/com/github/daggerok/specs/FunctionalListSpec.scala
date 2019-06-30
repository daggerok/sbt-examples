package com.github.daggerok.specs

import org.scalatest.FlatSpec

object FunctionalList {
  trait MyList[+T] {
    def head: T
    def tail: MyList[T]
    def isEmpty: Boolean
    def +[S >: T](element: S): MyList[S]
    def filter(myPredicate: T => Boolean): MyList[T]
    def map[S](transformer: T => S): MyList[S]
    def flatMap[S](transformer: T => MyList[S]): MyList[S]
    def ++[S >: T](myList: MyList[S]): MyList[S]
    def concat[S >: T](myList: MyList[S]): MyList[S] = ++(myList)
    def toStringBuilder: String
    override def toString: String = s"MyList($toStringBuilder)"
  }

  /*trait MyPredicate[-T] {
    def test(element: T): Boolean
  }

  trait MyTransformer[-T, S] {
    def map(element: T): S
  }*/

  case object MyEmptyList extends MyList[Nothing] {
    override def head: Nothing = throw new NoSuchElementException
    override def tail: MyList[Nothing] = throw new NoSuchElementException
    override def isEmpty: Boolean = true
    override def +[S >: Nothing](element: S): MyList[S] = MyListNode(element, this)
    override def toStringBuilder: String = ""
    override def filter(myPredicate: Nothing => Boolean): MyList[Nothing] = MyEmptyList
    override def map[S](transformer: Nothing => S): MyList[S] = MyEmptyList
    override def ++[S >: Nothing](myList: MyList[S]): MyList[S] = myList
    override def flatMap[S](transformer: Nothing => MyList[S]): MyList[S] = MyEmptyList
  }

  case class MyListNode[+T](head: T, tail: MyList[T] = MyEmptyList) extends MyList[T] {
    override def isEmpty: Boolean = false

    override def +[S >: T](element: S): MyList[S] = MyListNode(element, this)

    override def toStringBuilder: String = {
      def loop(acc: String, lt: MyList[T]): String =
        if (lt.isEmpty) acc
        else loop(s"$acc,${lt.head}", lt.tail)
      loop(s"$head", tail)
    }

    override def filter(myPredicate: T => Boolean): MyList[T] =
      if (myPredicate.apply(head)) MyListNode(head, tail.filter(myPredicate))
      else tail.filter(myPredicate)

    override def map[S](transformer: T => S): MyList[S] =
      MyListNode(transformer.apply(head), tail.map(transformer))

    override def ++[S >: T](myList: MyList[S]): MyList[S] =
      MyListNode(head, tail ++ myList)

    override def flatMap[S](transformer: T => MyList[S]): MyList[S] =
      transformer.apply(head) ++ tail.flatMap(transformer)
  }
}

/**
  * http://www.scalatest.org/user_guide/selecting_a_style
  */
class FunctionalListSpec extends FlatSpec {
  import FunctionalList._

  it should "have an empty list" in {
    assert(MyEmptyList.isEmpty)
  }

  it should "throw an no such element exceptions on empty list head or tail operations" in {
    assertThrows[NoSuchElementException] {
      MyEmptyList.head
    }
    assertThrows[NoSuchElementException] {
      MyEmptyList.tail
    }
  }

  it should "have non empty list on + (add) operation with" in {
    val nil: MyList[String] = MyEmptyList
    val list = nil + "ololo"

    assert(!list.isEmpty)
    assert(list.head == "ololo")
    assert(list.tail.isEmpty)

    println(list)
  }

  it should "have multi-node MyNodeList" in {
    val list = MyListNode(1.11, MyListNode(2.22, MyListNode(3.33)))

    assert(!list.isEmpty)
    assert(!list.tail.isEmpty)
    assert(!list.tail.tail.isEmpty)
    assert(list.tail.tail.tail.isEmpty)

    println(list)
  }

  it should "have result list with ++ (concat) operation" in {
    val list1 = MyListNode(1, MyListNode(2, MyListNode(3)))
    val list2 = MyListNode(2, MyListNode(1, MyEmptyList))
    val concat = list1 ++ list2
    println(s"concat: $concat")
  }

  it should "have filter functional monadic operation" in {
    val list = MyListNode(1, MyListNode(2, MyListNode(3, MyEmptyList)))
    val filtered = list.filter(_ % 2 == 0)

    assert(!filtered.isEmpty)
    assert(filtered.head == 2)
    assert(filtered.tail.isEmpty)

    println(s"filtered: $filtered")
  }

  it should "have map functional monadic operation" in {
    val list = MyListNode(1, MyListNode(2, MyListNode(3, MyEmptyList)))
    val transformed = list.map(_ + 1)

    assert(!transformed.isEmpty)
    assert(transformed.head == 2)

    assert(!transformed.tail.isEmpty)
    assert(transformed.tail.head == 3)

    assert(!transformed.tail.tail.isEmpty)
    assert(transformed.tail.tail.head == 4)

    println(s"transformed: $transformed")
  }

  it should "have flatMap functional monadic operation" in {
    val list = MyListNode(3, MyListNode(2, MyListNode(1)))
    /*val flatten = list.flatMap(new Function1[Int, MyList[Int]] {
      override def apply(element: Int): MyList[Int] = MyListNode(element - 1, MyListNode(element, MyListNode(element + 1)))
    })*/
    //val flatten = list.flatMap((element: Int) => MyListNode(element - 1, MyListNode(element, MyListNode(element + 1))))
    val flatten = list.flatMap(element =>
      MyListNode(element - 1, MyListNode(element, MyListNode(element + 1))))
    println(s"flatten: $flatten")
  }
}
