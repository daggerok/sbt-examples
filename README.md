# sbt-examples
Read documentation [here](https://daggerok.github.io/sbt-examples/) and learn baby, learn... GitHub Travis CI build status: [![Build Status](https://travis-ci.org/daggerok/sbt-examples.svg?branch=master)](https://travis-ci.org/daggerok/sbt-examples)

_table content in a reverse order_

[[toc]]

## idea-sbt project

```bash
cd ./idea-sbt/
# TDD
./sbtw ~test:test
```

## sbt getting-started

to quickly start new project, all you need to do is just:

```bash
mkdir hello-world
cd hello-world
. ../wrapper/sbt-wrapper
mkdir -p src/main/scala project
echo 'object Main extends App { println("Hello") }' > src/main/scala/Main.scala
echo 'sbt.version=1.2.8' > project/build.build.properties
./sbtw run
```

## sbt wrapper

to quickly add an sbt wrapper to your project, use `./wrapper/sbt-wrapper` script like so:

```bash
cd /path/to/my-project/
bash /path/to/wrapper/sbt-wrapper
./sbtw -v
```

_other repositories_

* [Akka Persistence | Scala | Jackson JSON Serialization | SBT GitHub: daggerok/akka-persistence-json-serializaer-example](https://github.com/daggerok/akka-persistence-json-serializaer-example)

_resources_

* [YouTube: A Simple Build Tool (SBT) video tutorial](https://www.youtube.com/watch?time_continue=41&v=LKkw140QmyU)

<!--
* [YouTube: Functional Programming Principles in Scala](https://www.youtube.com/channel/UC606CODOUaA3-E5LcC5yKAQ)
* [YouTube: Principles of Reactive Programming in Scala](https://www.youtube.com/playlist?list=PLMhMDErmC1TdBMxd3KnRfYiBV2ELvLyxN)
* [YouTube: Введение в язык программирования Scala](https://www.youtube.com/watch?v=EVz04VMtUfE)
-->
