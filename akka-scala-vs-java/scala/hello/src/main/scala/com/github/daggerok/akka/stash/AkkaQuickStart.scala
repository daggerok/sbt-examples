package com.github.daggerok.akka.stash

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

object Greeter {
  def props(message: String, printerActor: ActorRef): Props = Props(new Greeter(message, printerActor))
  final case class WhoToGreet(who: String)
  case object Greet
}

class Greeter(message: String, printerActor: ActorRef) extends Actor {
  import Greeter._
  import Printer._

  var greeting = ""

  def receive = {
    case WhoToGreet(who) =>
      greeting = message + ", " + who
    case Greet           =>
      printerActor ! Greeting(greeting)
  }
}

object Printer {
  def props: Props = Props[Printer]
  final case class Greeting(greeting: String)
}

class Printer extends Actor with ActorLogging {
  import Printer._

  def receive = {
    case Greeting(greeting) =>
      log.info("Greeting received (from " + sender() + "): " + greeting)
  }
}

object AkkaQuickStart extends App {
  import Greeter._

  val system: ActorSystem = ActorSystem("helloAkka")
  val printer: ActorRef = system.actorOf(Printer.props, "printerActor")

  val howdyGreeter: ActorRef = system.actorOf(Greeter.props("Howdy", printer), "howdyGreeter")
  val helloGreeter: ActorRef = system.actorOf(Greeter.props("Hello", printer), "helloGreeter")
  val goodDayGreeter: ActorRef = system.actorOf(Greeter.props("Good day", printer), "goodDayGreeter")

  howdyGreeter ! WhoToGreet("Akka")
  howdyGreeter ! Greet

  howdyGreeter ! WhoToGreet("Lightbend")
  howdyGreeter ! Greet

  helloGreeter ! WhoToGreet("Scala")
  helloGreeter ! Greet

  goodDayGreeter ! WhoToGreet("Play")
  goodDayGreeter ! Greet

  system.terminate()
}
