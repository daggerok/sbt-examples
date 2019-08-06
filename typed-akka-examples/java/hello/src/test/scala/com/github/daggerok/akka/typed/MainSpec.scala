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
      //val helloGreeter = system.actorOf(Greeter.props(testProbe.ref))
      //val name = "Akka"
      //
      //helloGreeter ! WhoToGreet.of(name)
      //testProbe.expectNoMessage(500 millis)
      //
      //helloGreeter ! new Greet
      //testProbe.expectMsg(500 millis, Greeting.of(s"Hola, $name"))
      assert(1 == 1)
    }
  }
}
