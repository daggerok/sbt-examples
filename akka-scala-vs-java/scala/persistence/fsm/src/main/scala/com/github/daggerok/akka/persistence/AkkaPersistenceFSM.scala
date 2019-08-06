package com.github.daggerok.akka.persistence

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.japi.Util
import akka.persistence.fsm.PersistentFSM
import akka.persistence.fsm.PersistentFSM.FSMState
import akka.persistence.{PersistentActor, SnapshotOffer}

import scala.reflect.ClassTag

object BankAccount {
  def props(id: BigInt): Props = Props(new BankAccount(id))
  // 1.1: state (FSM)
  sealed trait State extends FSMState
  case object Pending extends State {
    override def identifier = "PENDING"
  }
  case object Active extends State {
    override def identifier = "ACTIVE"
  }
  case object Inactive extends State {
    override def identifier = "INACTIVE"
  }

  // 1.2: data...
  sealed trait Data {
    val amount: BigDecimal
  }
  case class ZeroBalance(override val amount: BigDecimal = BigDecimal(0.00)) extends Data
  case class Balance(override val amount: BigDecimal) extends Data

  // 1.3
  sealed trait MoneyTransfer {
    val amount: BigDecimal
  }
  case class Credit(override val amount: scala.BigDecimal) extends MoneyTransfer
  case class Debit(override val amount: scala.BigDecimal) extends MoneyTransfer

  // 1.4: events (event sourcing)
  sealed trait DomainEvent // event-sourced events to be persisted
  case class AcceptedTransaction(transfer: MoneyTransfer) extends DomainEvent
  case class RejectedTransaction(transfer: MoneyTransfer, reason: String) extends DomainEvent

  // 1.5: commands
  sealed trait Command
  case class Deposit(credit: Credit) extends Command
  case class Withdrawal(debit: Debit) extends Command
  case class Activate(deposit: Deposit) extends Command
  case class Deactivate(reason: String) extends Command
}

import BankAccount._
class BankAccount(id: BigInt) extends PersistentFSM[State, Data, DomainEvent] with ActorLogging { // 2.1
  log.info("starting...")

  override def persistenceId: String = s"bank-account-$id" // unique per customer

  override implicit def domainEventClassTag: ClassTag[DomainEvent] = Util.classTag(classOf[DomainEvent])

  override def applyEvent(domainEvent: DomainEvent, currentData: Data): Data = {
    domainEvent match {
      case AcceptedTransaction(transfer: Credit) =>
        val newAmount = currentData.amount + transfer.amount
        log.info("{}: {} -> {}", transfer, currentData.amount, newAmount)
        Balance(newAmount)
      case AcceptedTransaction(transfer: Debit) =>
        val newAmount = currentData.amount - transfer.amount
        log.info("{}: {} -> {}", transfer, currentData.amount, newAmount)
        if (newAmount > 0) Balance(newAmount)
        else ZeroBalance()
      case RejectedTransaction(transfer, reason) =>
        log.warning("{} rejected: {}", transfer, reason)
        currentData
    }
  }

  startWith(Pending, ZeroBalance())

  when(Pending) {
    case Event(Deposit(credit), _) =>
      log.info("Account activation by given: {}", credit)
      goto(Active) applying(AcceptedTransaction(Credit(credit.amount)))
    case Event(withdrawal: Withdrawal, _) =>
      log.warning("Invariant: {}", withdrawal)
      stay.replying(stateData)
    case Event(Activate(deposit: Deposit), _) =>
      log.info("Account activation: {}", deposit)
      goto(Active) applying(AcceptedTransaction(Credit(deposit.credit.amount)))
    case Event(Deactivate(reason), _) =>
      log.warning("Account deactivation: {}", reason)
      goto(Inactive)
  }

  when(Active) {
    case Event(Deposit(credit), _) =>
      log.info("Account credit: {}", credit)
      goto(Active) applying(AcceptedTransaction(credit))
    case Event(Withdrawal(debit), _) =>
      log.warning("Account debit: {}", debit)
      stay.replying(stateData) applying(AcceptedTransaction(debit))
    case Event(activate: Activate, _) =>
      log.warning("Invariant: {}", activate)
      stay.replying(stateData) applying(RejectedTransaction(activate.deposit.credit, "Account is already active"))
    case Event(Deactivate(reason), _) =>
      log.warning("Account deactivation: {}", reason)
      goto(Inactive)
  }
}

object AkkaPersistenceFSM extends App {
  val system = ActorSystem("bankAccountPersistenceFSM")
  val bankAccountActor = system.actorOf(BankAccount.props(1), "bankAccountActor")

  bankAccountActor ! Activate(Deposit(Credit(3.10)))
  bankAccountActor ! Withdrawal(Debit(1.20)) // NACK
  bankAccountActor ! Deposit(Credit(2.30))
  bankAccountActor ! Withdrawal(Debit(3.40))
  bankAccountActor ! Withdrawal(Debit(2.30))
  bankAccountActor ! Deposit(Credit(1.20))
  bankAccountActor ! Withdrawal(Debit(1.10))

  Thread sleep 1234
  system.terminate()
}
