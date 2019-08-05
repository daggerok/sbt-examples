name := "akka-scala-vs-java"
ThisBuild / version := "1.0.0-SNAPSHOT"
ThisBuild / organization := "com.github.daggerok.akka"
ThisBuild / licenses := Seq(("MIT", url("https://github.com/daggerok/sbt-examples/blob/master/LICENSE")))

scalaVersion in ThisBuild := "2.13.0"
libraryDependencies in ThisBuild ++= commonLibraryDependencies

/* def projects, starts from root */

lazy val root =
  (project in file("."))
    .aggregate(refs: _*)
    .dependsOn(deps: _*)
    .settings(
      //commonSettings,
      update / aggregate := false,
    )

/* DRY */

lazy val refs = Array[ProjectReference](
  javaHello, javaStash, javaFSM,
  javaPersistenceCounter,
  scalaHello, scalaStash, scalaFSM,
  scalaPersistenceCounter,
)

lazy val deps: Array[ClasspathDep[ProjectReference]] =
  refs.map(ClasspathDependency(_, Option.empty))

/* def subProjects */

lazy val javaHello =
  (project in file("java/hello"))
    .settings(
      libraryDependencies ++= javaLibraryDependencies,
    )

lazy val javaStash =
  (project in file("java/stash"))
    .settings(
      libraryDependencies ++= javaLibraryDependencies,
    )

lazy val javaFSM =
  (project in file("java/fsm"))
    .settings(
      libraryDependencies ++= javaLibraryDependencies,
    )

lazy val javaPersistenceCounter =
  (project in file("java/persistence/counter"))
    .settings(
      libraryDependencies ++= javaAkkaPersistenceLibraryDependencies,
    )

lazy val scalaPersistenceCounter =
  (project in file("scala/persistence/counter"))
    .settings(
      libraryDependencies ++= akkaPersistenceLibraryDependencies,
    )

lazy val scalaFSM =
  (project in file("scala/fsm"))

lazy val scalaStash =
  (project in file("scala/stash"))

lazy val scalaHello =
  (project in file("scala/hello"))
//.settings(
//  commonSettings,
//  //mainClass in (Compile, run) := Some("com.github.daggerok.akka.quickstart.AkkaQuickStart")
//)

/* def common settings */

//lazy val commonSettings = Seq(
//  scalaVersion := globalScalaVersion,
//  organization := globalOrganization,
//  version := globalVersion,
//)

/* def dependencies */

lazy val akkaVersion = "2.6.0-M5"
lazy val commonLibraryDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
)

lazy val lombokVersion = "1.18.8"
lazy val slf4jVersion = "1.8.0-beta1" // "2.0.0-alpha0" // "1.7.26"
lazy val logbackVersion = "1.3.0-alpha4" // "1.2.3"
lazy val javaLibraryDependencies = Seq(
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.projectlombok" % "lombok" % lombokVersion,
  //"io.vavr" % "vavr" % vavrVersion,
)

//val jacksonVersion = "2.9.9"
lazy val akkaPersistenceLibraryDependencies = Seq(
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  //"com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  //"com.fasterxml.jackson.core"   %  "jackson-databind"     % jacksonVersion,
)

lazy val vavrVersion = "0.10.2"
lazy val javaAkkaPersistenceLibraryDependencies =
  javaLibraryDependencies ++
    akkaPersistenceLibraryDependencies ++
    Seq("io.vavr" % "vavr" % vavrVersion)

//lazy val myTask = taskKey[Unit]("my task")
//
//myTask := {
//  val comb = akkaPersistenceLibraryDependencies ++ javaLibraryDependencies
//  println(comb)
//}
