name := "typed-akka-examples"
ThisBuild / version := "1.0.0-SNAPSHOT"
ThisBuild / organization := "com.github.daggerok.akka.typed"

ThisBuild / scalaVersion := "2.13.0"
ThisBuild / libraryDependencies ++= commonLibraryDependencies

ThisBuild / licenses := Seq(("MIT", url("https://github.com/daggerok/sbt-examples/blob/master/LICENSE")))

/* def dependencies */

lazy val akkaVersion = "2.6.0-M5"
lazy val scalatestVersion = "3.1.0-SNAP13"
lazy val commonLibraryDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % Test,
)

lazy val lombokVersion = "1.18.8"
lazy val slf4jVersion = "1.8.0-beta1"
lazy val logbackVersion = "1.3.0-alpha4"
lazy val javaLibraryDependencies = Seq(
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.projectlombok" % "lombok" % lombokVersion,
)

//lazy val akkaPersistenceLibraryDependencies = Seq(
//  "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
//  "org.iq80.leveldb" % "leveldb" % "0.7",
//  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
//)
//
//lazy val javaAkkaPersistenceLibraryDependencies =
//  javaLibraryDependencies ++
//    akkaPersistenceLibraryDependencies ++
//    Seq("io.vavr" % "vavr" % "0.10.2")

/* def projects, starts from root */

lazy val root =
  (project in file("."))
    .aggregate(refs: _*)
    .dependsOn(deps: _*)
    .settings(
      update / aggregate := false,
    )

/* DRY */

lazy val refs = Array[ProjectReference](
  scalaAccount,
  scalaHello,
  javaHello,
  javaHola,
)

lazy val deps: Array[ClasspathDep[ProjectReference]] =
  refs.map(ClasspathDependency(_, Option.empty))

/* def subProjects */

lazy val scalaAccount =
  (project in file("scala/account"))

lazy val scalaHello =
  (project in file("scala/hello"))

lazy val javaHello =
  (project in file("java/hello"))
    .settings(
      libraryDependencies ++= javaLibraryDependencies
    )

lazy val javaHola =
  (project in file("java/hola"))
    .settings(
      libraryDependencies ++= javaLibraryDependencies
    )

/* def custom tasks */

//lazy val myTask = taskKey[Unit]("my task")
//
//myTask := {
//  val comb = akkaPersistenceLibraryDependencies ++ javaLibraryDependencies
//  println(comb)
//}
