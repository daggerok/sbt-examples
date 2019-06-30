package com.github.daggerok.specs

import org.scalatest.FlatSpec

object MyList {
  trait MyList[+T] {
    def head: T
    def tail: MyList[T]
    def isEmpty: Boolean
    def add[S >: T](element: S): MyList[S]
  }

  case object MyEmptyList extends MyList[Nothing] {
    override def head: Nothing = throw new NoSuchElementException
    override def tail: MyList[Nothing] = throw new NoSuchElementException
    override def isEmpty: Boolean = true
    override def add[S >: Nothing](element: S): MyList[S] = MyListNode(element, this)
    override def toString: String = "MyEmpty"
  }

  case class MyListNode[+T](head: T, tail: MyList[T]) extends MyList[T] {
    override def isEmpty: Boolean = false
    override def add[S >: T](element: S): MyList[S] = MyListNode(element, this)
    override def toString: String = s"MyList($head,$tail)"
  }
}

/**
  * http://www.scalatest.org/user_guide/selecting_a_style
  */
class ListSpec extends FlatSpec {
  import MyList._

  it should "have an empty MyEmptyList" in {
    assert(MyEmptyList.isEmpty)
  }

  it should "throw an no such element exceptions on empty MyEmptyList head or tail operations" in {
    assertThrows[NoSuchElementException] {
      MyEmptyList.head
    }
    assertThrows[NoSuchElementException] {
      MyEmptyList.tail
    }
  }

  it should "return non empty MyNodeList on add operation" in {
    val nil: MyList[String] = MyEmptyList
    val list = nil.add("ololo")
    assert(!list.isEmpty)
    assert(list.head == "ololo")
    assert(list.tail.isEmpty)
  }

  it should "have multi-node MyNodeList" in {
    val list = MyListNode(1, MyListNode(2, MyListNode(3, MyEmptyList)))
    assert(!list.isEmpty)
    assert(!list.tail.isEmpty)
    assert(!list.tail.tail.isEmpty)
    assert(list.tail.tail.tail.isEmpty)
  }
}
