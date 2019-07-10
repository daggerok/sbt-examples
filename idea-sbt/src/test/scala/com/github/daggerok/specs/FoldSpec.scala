package com.github.daggerok.specs

import org.scalatest.FlatSpec

/**
  * http://www.scalatest.org/user_guide/selecting_a_style
  */
class FoldSpec extends FlatSpec {

  "foldLeft" should "be possible" in {
    val combiner: (Int, Int) => Int = (x, y) => {
      println(s"foldLeft combining x = $x with y = $y")
      x + y
    }
    val zero = 0
    val result = Seq(1, 2, 3).foldLeft(zero)(combiner)
    assert(result == 6)
  }

  "foldRight" should "be possible too" in {
    val combiner: (Int, Int) => Int = (x, y) => {
      println(s"foldRight combining x = $x with y = $y")
      x + y
    }
    val zero = 0
    val result = Seq(1, 2, 3).foldRight(zero)(combiner)
    assert(result == 6)
  }
}
