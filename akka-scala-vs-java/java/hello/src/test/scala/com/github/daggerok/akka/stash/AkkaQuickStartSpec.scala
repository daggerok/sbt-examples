package com.github.daggerok.akka.stash

import org.scalatest.{ BeforeAndAfterAll, WordSpecLike, Matchers }
import akka.actor.ActorSystem
import akka.testkit.{ TestKit, TestProbe }
import scala.concurrent.duration._
import scala.language.postfixOps

class AkkaQuickStartSpec(_system: ActorSystem)
  extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("AkkaQuickStartSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Greeter Actor" should {
    "pass on a greeting message when instructed to" in {
      val testProbe = TestProbe()
      val helloGreeter = system.actorOf(Greeter.props(testProbe.ref))
      val name = "Akka"

      helloGreeter ! WhoToGreet.of(name)
      testProbe.expectNoMessage(500 millis)

      helloGreeter ! new Greet
      testProbe.expectMsg(500 millis, Greeting.of(s"Hola, $name"))
    }
  }
}
