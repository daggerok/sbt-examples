package com.github.daggerok.hello

class Hello {
  def greet(name: String) = s"Hello, $name!"
}

object Main extends App {
  private val hello = new Hello()
  println(hello.greet("Scala"))
}