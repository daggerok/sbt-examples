package com.github.daggerok.notations

import org.scalatest.FunSuite

class MethodsNotationsTest extends FunSuite {
  test("should have infix notation") {
    class Person(val name: String) {
      def plus(person: Person) = s"$name with ${person.name}"
      def +(person: Person) = s"$name + ${person.name} = love"
    }

    val mary = new Person("Mary")
    val bob = new Person("Bob")

    val regular = mary.plus(bob)
    assert(regular == "Mary with Bob")

    val infix = mary plus bob
    assert(infix == "Mary with Bob")

    val operator = mary + bob
    assert(operator == "Mary + Bob = love")
  }

  test("should have prefix notation") {
    class Person(name: String) {
      def unary_! : String = s"Bad, bad $name!"
    }

    val person = new Person("Max")
    assert("Bad, bad Max!" == !person)
  }

  test("should not use postfix notation...") {
    class Person(name: String) {
      def zzz: String = s"$name is sleeping..."
    }

    val max = new Person("Max")
    //val status = max zzz
    val status = max.zzz
    assert("Max is sleeping..." == status)

    import scala.language.postfixOps
    assert("Max is sleeping..." == (max zzz))
  }

  test("should have apply notation") {
    class Person(name: String) {
      def apply() = s"Hey, I'm $name!"
      def apply(times: Int, acc: String = ""): String =
        if (times < 1) acc
        else apply(times - 1, apply() + " " + acc)
    }

    val max = new Person("Max")
    assert("Hey, I'm Max!" == max())
    assert("Hey, I'm Max! Hey, I'm Max! " == max(2))
  }
}
