package com.github.daggerok.specs

import org.scalatest.FlatSpec

/**
  * http://www.scalatest.org/user_guide/selecting_a_style
  */
class TemplateSpec extends FlatSpec {

  "first test" should "just assert" in {
    assert(Map.empty.isEmpty)
  }

  "second test" should "just verify exception to be thrown" in {
    assertThrows[RuntimeException] {
      throw new RuntimeException
    }
  }

  it should "have a test" in {
    assert(1 == 1)
  }

  it should "have another test" in {
    assert(1 != 2)
  }
}
