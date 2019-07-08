package com.github.daggerok.specs

import org.scalatest.FlatSpec

trait Subscriber {
  def handler(publisher: Publisher): Unit
}

trait Publisher {
  private var subscribers: Set[Subscriber] = Set()
  def subscribe(subscriber: Subscriber): Unit = subscribers += subscriber
  def unsubscribe(subscriber: Subscriber): Unit = subscribers -= subscriber
  def publish(): Unit = subscribers.foreach(_.handler(this))
}

class BankAccount extends Publisher {
  private var balance = 0
  def getBalance: Int = balance
  def deposit(amount: Int): Unit = if (amount > 0) {
    balance += amount
    publish()
  }
  def withdraw(amount: Int): Unit =
    if (0 < amount && amount <= balance) {
      balance -= amount
      publish()
    }
    else throw new RuntimeException("insufficient amount")
}

class Bank(observed: Seq[BankAccount]) extends Subscriber {
  observed.foreach(_.subscribe(this))
  private var total: Int = _
  compute()
  private def compute(): Unit =
    total = observed.map(_.getBalance).sum
  override def handler(publisher: Publisher): Unit = compute()
  def getTotal: Int = total
}

/**
  * http://www.scalatest.org/user_guide/selecting_a_style
  */
class PubSubSpec extends FlatSpec {
  it should "have zero balance on just created bank account" in {
    val bankAccount = new BankAccount
    assert(bankAccount.getBalance == 0)
  }

  it should "deposit" in {
    val bankAccount = new BankAccount
    bankAccount.deposit(25)
    assert(bankAccount.getBalance == 0 + 25)
  }

  it should "withdraw" in {
    val bankAccount = new BankAccount
    bankAccount.deposit(25)
    bankAccount.withdraw(10)
    assert(bankAccount.getBalance == 25 - 10)
  }

  it should "interact" in {
    val alex = new BankAccount
    val bob = new BankAccount
    val bank = new Bank(List(alex, bob))
    assert(bank.getTotal == 0)

    bob.deposit(25)
    alex.deposit(10)
    assert(bank.getTotal == 25 + 10)
  }
}
