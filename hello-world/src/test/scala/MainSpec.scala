import org.scalatest._

class MainSpec extends FunSuite with DiagrammedAssertions {
  test("Hello should start with Hell") {
    // Hello, as opposed to hello
    assert("Hello".startsWith("Hell"))
  }
}
