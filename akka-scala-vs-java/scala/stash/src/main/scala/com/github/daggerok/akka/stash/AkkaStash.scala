package com.github.daggerok.akka.stash

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Stash}

case class User(name: String, age: Int)

object UserRepositoryActor {
  sealed trait Messages
  case object Connect extends Messages
  case object Disconnect extends Messages

  sealed trait DbOperations extends Messages
  case class Save(user: User) extends DbOperations
  case class Delete(user: User) extends DbOperations
  //etc...
}

class UserRepositoryActor extends Actor with ActorLogging with Stash {
  import UserRepositoryActor._

  override def receive: Receive = disconnected

  def disconnected: Receive = {
    case Connect =>
      log.info("connected!")
      unstashAll()
      context.become(connected)
    case _ =>
      log.info("received a message when disconnected!")
      stash()
  }

  def connected: Receive = {
    case Disconnect =>
      log.info("disconnect!")
      context.unbecome()
    case op: DbOperations =>
      log.info("received: {}", op)
  }
}

object AkkaStash extends App {
  val system: ActorSystem = ActorSystem("stashAkka")
  val userRepositoryActor = system.actorOf(Props[UserRepositoryActor], "userRepositoryActor")

  import UserRepositoryActor._
  userRepositoryActor ! Save(User("max", 123))
  userRepositoryActor ! Connect
  userRepositoryActor ! Save(User("bob", 321))
  userRepositoryActor ! Delete(User("bob", 321))
  userRepositoryActor ! Disconnect

  //Thread.sleep(123)
  system.terminate()
}
