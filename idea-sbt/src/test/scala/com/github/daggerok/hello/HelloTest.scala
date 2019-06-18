package com.github.daggerok.hello

import org.scalatest.FunSuite

class HelloTest extends FunSuite {
  test("should say hello") {
    val hello = new Hello
    val greeting = hello.greet("Maksimko")
    assert(greeting == "Hello, Maksimko!")
  }
}
