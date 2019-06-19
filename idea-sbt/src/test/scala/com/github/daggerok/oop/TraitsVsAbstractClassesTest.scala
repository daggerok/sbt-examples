package com.github.daggerok.oop

import org.scalatest.FunSuite

class TraitsVsAbstractClassesTest extends FunSuite {
  test("traits shouldn't have parametrized constructors, should pass arguments to parent constructors") {
    abstract class Animal(kind: String)
    class Cat extends Animal("cat")
    class Dog extends Animal("dog")

    trait Wild//(kind: String)
    trait Home
  }

  test("multiple traits inheritance by same class") {
    abstract class Animal
    trait CanFly
    trait CanSwim
    class Duck extends Animal with CanFly with CanSwim
  }

  test("traits - behavior (describe what they do), abstract class - thing (describe what is it)") {
    abstract class IamIsSomeone
    trait AndICanDoSomething
    class Me extends IamIsSomeone with AndICanDoSomething
  }
}
