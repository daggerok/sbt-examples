package com.github.daggerok.oop

import org.scalatest.FunSuite

class InheritanceTest extends FunSuite {
  test("shouldn't extends from final class") {
    final class Animal
    class Cat/* extends Animal*/
  }

  test("shouldn't override final method") {
    class Animal {
      final def kind: String = "home"
    }
    class Can extends Animal {
      //override def kind: String = "mew"
    }
  }

  test("shouldn't override final fields") {
    class Animal {
      final val kind: String = "home"
    }
    class Can extends Animal {
      //override val kind: String = "mew"
    }
  }

  test("sealed classes should extends only in same file") {
    sealed class Animal
    class Cat extends Animal
    class Dog extends Animal
    // it's not possible to extends from Animal class in another file...
  }
}
