name := "akka-scala-vs-java"
ThisBuild / version := "1.0.0-SNAPSHOT"
ThisBuild / organization := "com.github.daggerok.akka"
scalaVersion in ThisBuild := "2.13.0"
libraryDependencies in ThisBuild ++= commonLibraryDependencies

lazy val root =
  (project in file("."))
    .aggregate(javaHello, scalaHello)
    .dependsOn(javaHello, scalaHello)
    .settings(
      //commonSettings,
      update / aggregate := false,
    )

lazy val javaHello =
  (project in file("java/hello"))
    .settings(
      libraryDependencies ++= javaLibraryDependencies,
    )

lazy val scalaHello =
  (project in file("scala/hello"))
//.settings(
//  commonSettings,
//  //mainClass in (Compile, run) := Some("com.github.daggerok.akka.quickstart.AkkaQuickStart")
//)

//lazy val commonSettings = Seq(
//  scalaVersion := globalScalaVersion,
//  organization := globalOrganization,
//  version := globalVersion,
//)

lazy val akkaVersion = "2.6.0-M5"
lazy val commonLibraryDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
)

lazy val vavrVersion = "0.10.2"
lazy val lombokVersion = "1.18.8"
lazy val slf4jVersion = "1.8.0-beta1" // "2.0.0-alpha0" // "1.7.26"
lazy val logbackVersion = "1.3.0-alpha4" // "1.2.3"
lazy val javaLibraryDependencies = Seq(
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.projectlombok" % "lombok" % lombokVersion,
  "io.vavr" % "vavr" % vavrVersion,
)
