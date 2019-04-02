name := """randolivetiles"""
organization := "ppsfleet"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"
val elastic4sVersion = "6.5.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
libraryDependencies += ws
libraryDependencies += ehcache
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "ppsfleet.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "ppsfleet.binders._"
