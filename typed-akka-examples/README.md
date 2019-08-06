# typed akka [![Build Status](https://travis-ci.org/daggerok/sbt-examples.svg?branch=master)](https://travis-ci.org/daggerok/sbt-examples)

with __java__ use:

```java
//...
import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
//...
```

with __scala__ use:

```scala
//...
import akka.actor.typed._
import akka.actor.typed.scaladsl._
//...
```

```shell script
./sbtw "project javaHello" clean run
./sbtw "project scalaHello" clean run
```

links:

* [Akka typed reference](https://doc.akka.io/docs/akka/current/typed/guide/tutorial_1.html)
* [Typed akka Java example](https://doc.akka.io/docs/akka/current/typed/actor-lifecycle.html)
