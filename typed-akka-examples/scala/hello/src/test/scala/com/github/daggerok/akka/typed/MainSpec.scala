package com.github.daggerok.akka.typed

import org.scalatest.{BeforeAndAfterAll, Matchers}
import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._
import scala.language.postfixOps

class MainSpec(_system: ActorSystem)
  extends TestKit(_system)
  with Matchers
  with AnyWordSpecLike
  with BeforeAndAfterAll {
  def this() = this(ActorSystem("TypedAkkaSpec"))
  override def afterAll: Unit = {
    shutdown(system)
  }

  "An Actor" should {
    "should work as expected" in {
      //val testProbe = TestProbe()
      //val helloGreetingMessage = "hello"
      //val helloGreeter = system.actorOf(Greeter.props(helloGreetingMessage, testProbe.ref))
      //val greetPerson = "Akka"
      //
      //helloGreeter ! WhoToGreet(greetPerson)
      //helloGreeter ! Greet
      //testProbe.expectMsg(500 millis, Greeting(helloGreetingMessage + ", " + greetPerson))
      assert(0 != 1)
    }
  }
}
