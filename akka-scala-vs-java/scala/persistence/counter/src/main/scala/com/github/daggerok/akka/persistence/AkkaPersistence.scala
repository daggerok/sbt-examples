package com.github.daggerok.akka.persistence

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}

object Counter {
  def props: Props = Props[Counter]
  // 1:
  sealed trait Operation { val counter: BigInt }
  case class Increment(override val counter: BigInt) extends Operation
  case class Decrement(override val counter: BigInt) extends Operation

  case class State(counter: BigInt)

  case class Cmd(op: Operation)

  sealed trait Event
  case class Evt(op: Operation) extends Event
}

class Counter extends PersistentActor with ActorLogging { // 2
  import Counter._
  log.info("starting...")

  private var state = State(counter = 0)

  private def updateState(event: Event): Unit = event match {
    case  Evt(Increment(counter)) =>
      val prev = State(state.counter)
      state = State(counter = state.counter + counter)
      log.info("update state: {} -> {}", prev.counter, state.counter)
    case Evt(Decrement(counter)) =>
      val prev = State(state.counter)
      state = State(counter = state.counter - counter)
      log.info("update state: {} -> {}", prev.counter, state.counter)
  }

  // 3: required to be unique!
  override def persistenceId: String = "global-counter"

  // 4: recovery mood:
  override def receiveRecover: Receive = {
    case event: Event =>
      val prev = state
      updateState(event)
      log.info("recover: {} -> {}", prev, state)
    case SnapshotOffer(_, snapshot: State) =>
      log.info("recover from snapshot: {} -> {}", state, snapshot)
      state = snapshot
  }

  // 5: normal mood:
  override def receiveCommand: Receive = {
    case cmd @ Cmd(op) =>
      log.info("handle command for operation: {}", op)
      persist(Evt(op)) { event =>
        log.info("event {} persisted", event)
        updateState(event)
      }
    case _ =>
      log.info("current state is: {}", state)
      sender() ! state
  }
}

object AkkaPersistence extends App {
  val system = ActorSystem("counterPersistenceAkka")
  val counterActor = system.actorOf(Counter.props, "counterActor")

  import Counter._
  counterActor ! Cmd(Increment(1))
  counterActor ! Cmd(Decrement(0))
  counterActor ! Cmd(Increment(2))
  counterActor ! Cmd(Decrement(1))
  counterActor ! "print" // +2

  Thread sleep 1234
  system.terminate()
}
