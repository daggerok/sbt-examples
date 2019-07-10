package com.github.daggerok.specs

import org.scalatest.FlatSpec

import scala.util.{Failure, Random, Success, Try}

/**
  * http://www.scalatest.org/user_guide/selecting_a_style
  */
class MonadsAndEffectsSpec extends FlatSpec {

  it should "have old style blocking adventure game" in {
    class Coin
    class Treasure
    trait Adventure {
      def collectCoins: List[Coin]
      def buyTreasure(coins: List[Coin]): Treasure
    }
    class MyAdventure extends Adventure {
      def collectCoins: List[Coin] = List()
      def buyTreasure(coins: List[Coin]): Treasure = coins match {
        case Nil => throw new RuntimeException
        case _ => new Treasure
      }
    }

    val adventure = new MyAdventure
    val coins = adventure.collectCoins
    assertThrows[RuntimeException](adventure.buyTreasure(coins))

    assert(null != adventure.buyTreasure(List(new Coin)))
  }

  it should "have Try monad styled adventure game" in {
    class Coin
    class Treasure
    trait Adventure {
      def collectCoins: Try[List[Coin]]
      def buyTreasure(coins: List[Coin]): Try[Treasure]
    }
    class MyAdventure extends Adventure {
      private val random = new Random
      def collectCoins: Try[List[Coin]] =
        if (random.nextBoolean()) Success(List(new Coin))
        else Failure(new RuntimeException)
      def buyTreasure(coins: List[Coin]): Try[Treasure] = coins match {
        case Nil => Failure(new RuntimeException)
        case _ => Success(new Treasure)
      }
    }

    val adventure = new MyAdventure
    val maybeCoins = adventure.collectCoins
    val maybeTreasure = maybeCoins match {
      case Success(coins) => adventure.buyTreasure(coins)
      case Failure(exception) => Failure(exception)
    }

    assert(
      (maybeTreasure.isSuccess && maybeCoins.isSuccess) ||
      (maybeTreasure.isFailure && maybeCoins.isFailure)
    )

    assert(adventure.buyTreasure(List.empty).isFailure)
    assert(adventure.buyTreasure(List(new Coin)).isSuccess)

    val maybeHappyPath = adventure.collectCoins
      .flatMap(coins => adventure.buyTreasure(coins))
    assert(maybeHappyPath.isSuccess || maybeHappyPath.isFailure)

    val comprehensivePath = for {
      coins <- adventure.collectCoins
      treasure <- adventure.buyTreasure(coins)
    } yield treasure
    assert(comprehensivePath.isSuccess || comprehensivePath.isFailure)
  }
}
