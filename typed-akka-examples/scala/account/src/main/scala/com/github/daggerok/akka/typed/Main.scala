package com.github.daggerok.akka.typed

import akka.actor.typed._
import akka.actor.typed.scaladsl._

/**
 * Account should have next functionality:
 * - get balance
 * - withdraw
 * - deposit
 *
 * Replay with reason in case if insufficient withdraw amount has been requested
 */
object Account {

  final case class Balance(amount: BigDecimal)

  final case object Deposited

  sealed trait WithdrawResponse
  final case object Withdrawn extends WithdrawResponse
  final case class InsufficientWithdraw(reason: String) extends WithdrawResponse

  sealed trait Command
  final case class GetBalance(replyTo: ActorRef[Balance]) extends Command
  final case class Deposit(amount: BigDecimal, replyTo: ActorRef[Deposited.type]) extends Command
  final case class Withdraw(amount: BigDecimal, replyTo: ActorRef[WithdrawResponse]) extends Command

  def apply(balance: BigDecimal): Behavior[Command] = Behaviors.receiveMessage {

    case GetBalance(replyTo) =>
      replyTo ! Balance(balance)
      Behaviors.same

    case Deposit(amount, replyTo) =>
      replyTo ! Deposited
      apply(balance + amount)

    case Withdraw(amount, replyTo) if amount > balance =>
      replyTo ! InsufficientWithdraw(s"requested amount ($amount) is greater than current balance")
      Behaviors.same

    case Withdraw(amount, replyTo) =>
      replyTo ! Withdrawn
      apply(balance - amount)
  }
}

object Transfer {

  import Account._

  sealed trait Command
  final case object HandleInsufficientWithdraw extends Command
  final case object HandleWithdrawn extends Command
  final case object HandleDeposited extends Command

  def apply(amount: BigDecimal, from: ActorRef[Withdraw], to: ActorRef[Deposit]): Behavior[Command] =
    Behaviors.setup { context =>

      val selfName = context.self.path.name
      val fromName = from.path.name
      val toName = to.path.name
      val log = context.log

      log.info("{}: started transfer {} {} -> {}", selfName, amount, fromName, toName)

      from ! Withdraw(amount, context.messageAdapter({
        case InsufficientWithdraw(reason) =>
          log.error("{}: InsufficientWithdraw! {}", fromName, reason)
          HandleInsufficientWithdraw

        case Withdrawn =>
          log.info("{}: withdrawn success! continue with {} deposit...", fromName, toName)
          HandleWithdrawn
      }))

      Behaviors.receivePartial {
        case (_, HandleInsufficientWithdraw) =>
          log.error("{}: abort on insufficient amount: {}", selfName, amount)
          Behaviors.stopped

        case (context, HandleWithdrawn) =>
          log.info("{}: deposit {} to {}", context.self.path.name, amount, toName)
          to ! Deposit(amount, context.messageAdapter(_ => HandleDeposited))

          Behaviors.receiveMessagePartial {
            case HandleDeposited =>
              context.log.info("{} success transfer {} from {} to {}",
                context.self.path.name, amount, fromName, toName)
              Behaviors.stopped
          }
      }
    }
}

object Main extends App {

  sealed trait Command

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      val from = context.spawn(Account(1000.00), "fromAccount")
      val to = context.spawn(Account(0.00), "toAccount")
      val transfer = context.spawn(Transfer(100.00, from, to), "moneyTransfer")

      context.watch(transfer)

      Behaviors.receiveSignal {
        case (_, Terminated(`transfer`)) =>
          Behaviors.stopped
      }
    }

  ActorSystem(Main(), "bankingSystem")
}
