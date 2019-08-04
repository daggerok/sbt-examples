package com.github.daggerok.akka.fsm

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, FSM, Props, Stash}

case class User(name: String, age: Int)

object FiniteStateMachineUserRepositoryActor {
  sealed trait State //1.1
  case object Connected extends State
  case object Disconnected extends State

  sealed trait Data //1.2
  case object Empty extends Data

  sealed trait Messages
  case object Connect extends Messages
  case object Disconnect extends Messages

  sealed trait DbOperations
  case class Save(user: User) extends DbOperations
  case class Delete(user: User) extends DbOperations
  //etc...
}

import FiniteStateMachineUserRepositoryActor._

class FiniteStateMachineUserRepositoryActor extends FSM[State, Data] /* 2.1 */ with ActorLogging with Stash {

  //2.2: define startWith
  startWith(
    Disconnected, //state
    Empty,        //data
    None          //timeout
  )

  //2.3: define our states...
  when(Disconnected) {
    case Event(Connect, _) =>
      log.info("connected!")
      unstashAll()
      goto(Connected) using(Empty)
    case Event(_, _) =>
      log.info("received a message when disconnected!")
      stash()
      stay using(Empty)
  }

  when(Connected) {
    case Event(Disconnect, _) =>
      log.info("disconnect!")
      context.unbecome()
      goto(Disconnected) using(Empty)
    case Event(op: DbOperations, _) =>
      log.info("received: {}", op)
      stay using(Empty)
  }

  //2.4: Finally, implement initialization...
  initialize()
}

object AkkaFSM extends App {
  val system: ActorSystem = ActorSystem("finiteStateMachine")
  val userRepositoryActor = system.actorOf(Props[FiniteStateMachineUserRepositoryActor], "finiteStateMachineUserRepositoryActor")

  import FiniteStateMachineUserRepositoryActor._
  userRepositoryActor ! Save(User("max", 123))
  userRepositoryActor ! Connect
  userRepositoryActor ! Save(User("bob", 321))
  userRepositoryActor ! Delete(User("bob", 321))
  userRepositoryActor ! Disconnect

  Thread sleep 123
  system.terminate()
}
