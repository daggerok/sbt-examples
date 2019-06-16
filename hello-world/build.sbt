ThisBuild / scalaVersion := "2.12.7"

ThisBuild / organization := "com.example"

lazy val helloWorld = (project in file("."))
  .settings(
    name := "hello-world",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  )