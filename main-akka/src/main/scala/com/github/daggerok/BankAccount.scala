package com.github.daggerok

import akka.actor._

object Sleep {
  def a(ms: Int = 123) = Thread.sleep(123)
}

object BankAccount {
  case class Deposit(amount: BigInt) {
    require(amount > 0, "deposit amount may not be non positive")
  }
  case class Withdraw(amount: BigInt) {
    require(amount > 0, "withdraw amount may not be non positive")
  }
  case object Succeed
  case object Failed
}

class BankAccount extends Actor  with ActorLogging {
  import BankAccount._

  override def receive: Receive = initWith(0)

  def initWith(balance: BigInt): Receive = {
    case Deposit(amount) =>
      Sleep.a()
      log.info("{}", Deposit(amount))
      sender ! Succeed
      context.become(initWith(amount))
    case Withdraw(amount) if balance >= amount =>
      Sleep.a()
      log.info("{}", Withdraw(amount))
      sender ! Succeed
      context.become(initWith(balance - amount))
    case msg =>
      Sleep.a()
      log.info("otherwise {}", msg)
      sender ! Failed
  }
}

class BankAccountTest extends Actor with ActorLogging {
  import BankAccount._

  private val bankAccount: ActorRef = context.actorOf(Props[BankAccount], "bankAccount")

  self ! "start"

  override def receive: Receive = {
    case "start" =>
      log.info("start 1!")
      bankAccount ! Withdraw(1)
      Sleep.a()
      bankAccount ! Deposit(10)
      Sleep.a()
      bankAccount ! Withdraw(5)
      Sleep.a()
      bankAccount ! Withdraw(5)
      Sleep.a()
      bankAccount ! Withdraw(1)
      Sleep.a()
      self ! "stop"
    case "stop" =>
      Sleep.a(4000)
      log.info("stop 1!")
      context.stop(bankAccount)
      context.stop(self)
      //context.system.terminate()
    case msg => log.info("response: {}", msg)
  }
}

////

object Transaction {
  case class MoneyTransfer(from: ActorRef, to: ActorRef, amount: BigInt) {
    require(from != null, "from may not be null")
    require(to != null, "to may not be null")
    require(amount > 0, "amount may not be non positive")
  }
  case object TransferSucceed
  case object TransferFailed
}

class Transaction extends Actor with ActorLogging {
  import Transaction._
  import BankAccount._

  override def receive: Receive = awaitForMoneyTransfer

  def awaitForMoneyTransfer: Receive = {
    case MoneyTransfer(from, to, amount) =>
      Sleep.a()
      from ! Deposit(amount)
      context.become(awaitForInitialTestDeposit(from, to, amount, sender))
    case _ => stopIfFailed(sender)
  }

  def awaitForInitialTestDeposit(from: ActorRef, to: ActorRef, amount: BigInt, sender: ActorRef): Receive = {
    case Succeed =>
      Sleep.a()
      from ! Withdraw(amount)
      context.become(awaitForWithdrawal(to, amount, sender))
    case _ => stopIfFailed(sender)
  }

  def awaitForWithdrawal(to: ActorRef, amount: BigInt, sender: ActorRef): Receive = {
    case Succeed =>
      Sleep.a()
      to ! Deposit(amount)
      context.become(awaitForDeposit(sender))
    case _ => stopIfFailed(sender)
  }

  def awaitForDeposit(sender: ActorRef): Receive = {
    case Succeed =>
      Sleep.a()
      sender ! TransferSucceed
      context.stop(self)
    case _ => stopIfFailed(sender)
  }

  def stopIfFailed(sender: ActorRef): Unit = {
    Sleep.a()
    sender ! TransferFailed
    context.stop(self)
  }
}

class MoneyTransferTest extends Actor with ActorLogging {
  import Transaction._
  import BankAccount._

  private val transfer: ActorRef = context.actorOf(Props[Transaction], "transfer")
  private val from: ActorRef = context.actorOf(Props[BankAccount], "from")
  private val to: ActorRef = context.actorOf(Props[BankAccount], "to")

  self ! "start"

  override def receive: Receive = {
    case "start" =>
      Sleep.a()
      log.info("start 2!")
      transfer ! MoneyTransfer(from, to, 5)
    case TransferSucceed =>
      Sleep.a()
      log.info("stop 2 success")
      self ! "stop"
    case TransferFailed =>
      Sleep.a()
      log.info("stop 2 failed")
      self ! "stop"
    case "stop" =>
      Sleep.a(5000)
      log.info("stop 2!")
      context.stop(from)
      context.stop(to)
      context.stop(transfer)
      context.stop(self)
    //context.system.terminate()
    case msg => log.info("response: {}", msg)
  }
}

////

object Main extends App {
  akka.Main.main(Array("com.github.daggerok.BankAccountTest"))
  akka.Main.main(Array("com.github.daggerok.MoneyTransferTest"))
}
