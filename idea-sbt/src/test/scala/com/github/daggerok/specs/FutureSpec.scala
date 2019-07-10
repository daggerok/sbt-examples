package com.github.daggerok.specs

import org.scalatest.FlatSpec

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * http://www.scalatest.org/user_guide/selecting_a_style
  */
class FutureSpec extends FlatSpec {

  def name: Future[String] = Future("Maksimko")
  def greetings(name: String): Future[List[String]] =
    Future(List(s"Hola, $name!"))

  it should "do flatMap Future" in {
    val flatMappedFuture = name.flatMap(name => greetings(name))
    val list = Await.result(flatMappedFuture, 1.seconds)
    assert(list == List("Hola, Maksimko!"))
  }

  it should "do for Future" in {
    val forFuture = for {
      n <- name
      list <- greetings(n)
    } yield list

    forFuture.foreach(list => assert(list == List("Hola, Maksimko!")))

    val list = Await.result(forFuture, 1.seconds)
    assert(list == List("Hola, Maksimko!"))
  }
}
