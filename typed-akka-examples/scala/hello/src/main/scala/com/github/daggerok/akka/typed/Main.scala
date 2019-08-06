package com.github.daggerok.akka.typed

import akka.actor.typed._
import akka.actor.typed.scaladsl._

object ChildActor {
  def apply(): Behavior[String] = Behaviors.setup(context => new ChildActor(context))
}

class ChildActor(context: ActorContext[String]) extends AbstractBehavior[String] {
  val log: Logger = context.log

  override def onMessage(msg: String): Behavior[String] = msg match {
    case "print" =>
      val secondRef = context.spawn(Behaviors.empty[String], "2nd-actor")
      log.info("Second: {}", secondRef)
      this
  }
}

object ParentActor {
  def apply(): Behavior[String] =
    Behaviors.setup(context => new ParentActor(context))
}

class ParentActor(context: ActorContext[String]) extends AbstractBehavior[String] {

  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "start" =>
        val firstRef = context.spawn(ChildActor(), "1st-actor")
        context.log.info("First: {}", firstRef)
        firstRef ! "print"
        this
    }
}

object Main extends App {
  private val system = ActorSystem(ParentActor(), "typedScalaSystem")

  system ! "start"
  //Thread sleep 1234
  system.terminate()
}
