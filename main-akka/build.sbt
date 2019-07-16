name := "main-akka"

version := "0.1"

scalaVersion := "2.13.0"

lazy val akkaVersion = "2.6.0-M4"
lazy val scalatestVersion = "3.0.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "org.scalactic" %% "scalactic" % scalatestVersion % Test,
  "org.scalatest" %% "scalatest" % scalatestVersion % Test,
)

lazy val commonSettings = Seq(
  organization := "com.github.daggerok",
  test in assembly := {}
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    mainClass in assembly := Some("com.github.daggerok.Main"),
    // more settings here ...
  )