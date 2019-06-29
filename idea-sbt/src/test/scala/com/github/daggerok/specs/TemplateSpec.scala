package com.github.daggerok.specs

import org.scalatest.FlatSpec

class TemplateSpec extends FlatSpec {
  "first test" should "just assert" in {
    assert(Map.empty.isEmpty)
  }

  "second test" should "just verify exception to be thrown" in {
    assertThrows[RuntimeException] {
      throw new RuntimeException
    }
  }
}
