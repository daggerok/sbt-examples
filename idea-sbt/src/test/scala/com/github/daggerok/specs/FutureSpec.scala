package com.github.daggerok.specs

import org.scalatest.FlatSpec

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.util.{Failure, Random}

/**
  * http://www.scalatest.org/user_guide/selecting_a_style
  */
class FutureSpec extends FlatSpec {

  def name: Future[String] = Future("Maksimko")
  def greetings(name: String): Future[List[String]] =
    Future(List(s"Hola, $name!"))

  it should "do flatMap Future" in {
    val flatMappedFuture = name.flatMap(name => greetings(name))
    val list = Await.result(flatMappedFuture, 1 seconds)
    assert(list == List("Hola, Maksimko!"))
  }

  it should "do for Future" in {
    val forFuture = for {
      n <- name
      list <- greetings(n)
    } yield list

    forFuture.foreach(list => assert(list == List("Hola, Maksimko!")))

    val list = Await.result(forFuture, 1 seconds)
    assert(list == List("Hola, Maksimko!"))
  }

  //"Fuck that shit! Future" should "have be able to retry with recover and foldLeft" in {
  //  object CanFail {
  //    private val random = new Random
  //    def maybeHello(name: String): Future[String] = Future[String] {
  //      if (random.nextBoolean()) s"Hello, $name!"
  //      else throw new RuntimeException("bye...")
  //    }
  //    def retry[T](times: Int = 1)(block: => Future[T]): Future[T] = {
  //      val failure: Future[Nothing] = Future.failed(new RuntimeException(
  //        s"sorry, I tried $times, but not got lucky..."))
  //      if (times < 1) failure
  //      else {
  //        val retries = (0 to times).map(_ => () => block)
  //        val result = retries
  //          //.foldLeft(failure)((failed, blck) => () => { failed.recoverWith { blck() } })
  //          .foldRight(() => failure)((blck, failed) => () => { blck() fallbackTo { failed() } })
  //        result ()
  //      }
  //    }
  //  }
  //  import CanFail._
  //}
}
