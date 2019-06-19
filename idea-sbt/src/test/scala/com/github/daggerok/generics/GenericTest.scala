package com.github.daggerok.generics

import org.scalatest.FunSuite

class GenericTest extends FunSuite {
  test("should have non generic (integer) implementation") {
    abstract class MyListOfIntegers {
      def head: Int
      def tail: MyListOfIntegers
      def isEmpty: Boolean
      def add(element: Int): MyListOfIntegers
    }
    object Empty extends MyListOfIntegers {
      override def head: Int = throw new NoSuchElementException
      override def tail: MyListOfIntegers = throw new NoSuchElementException
      override def isEmpty: Boolean = true
      override def add(element: Int): MyListOfIntegers = Node(element, Empty)
    }
    case class Node(head: Int, tail: MyListOfIntegers) extends MyListOfIntegers {
      override def isEmpty: Boolean = false
      override def add(element: Int): MyListOfIntegers = Node(element, this)
    }
    val myListOfIntegers = Node(1, Node(2, Node(3, Node(4, Empty))))
    assert(myListOfIntegers.head == 1)
    assert(myListOfIntegers.tail.head == 2)
    assert(myListOfIntegers.tail.tail.head == 3)
    assert(myListOfIntegers.tail.tail.tail.head == 4)
    assert(myListOfIntegers.tail.tail.tail.tail.isEmpty)
  }

  test("should have generic implementation") {
    abstract class MyGenericList[+A] {
      def head: A
      def tail: MyGenericList[A]
      def isEmpty: Boolean
      def add[B >: A](element: B): MyGenericList[B]
    }
    object Empty extends MyGenericList[Nothing] {
      def head: Nothing = throw new NoSuchElementException
      def tail: MyGenericList[Nothing] = throw new NoSuchElementException
      def isEmpty: Boolean = true
      def add[B >: Nothing](element: B): MyGenericList[B] = new Node[B](element, Empty)
    }
    case class Node[+A](head: A, tail: MyGenericList[A]) extends MyGenericList[A] {
      def isEmpty: Boolean = false
      def add[B >: A](element: B): MyGenericList[B] = Node(element, this)
    }

    val myIntegers = Node(1, Node(2, Empty))
    assert(myIntegers.head == 1)
    assert(myIntegers.tail.head == 2)
    assert(myIntegers.tail.tail.isEmpty)

    val myStrings = Node("one", Node("two", Empty))
    assert(myStrings.head == "one")
    assert(myStrings.tail.head == "two")
    assert(myStrings.tail.tail.isEmpty)
  }
}
