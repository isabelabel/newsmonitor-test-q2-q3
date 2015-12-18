name := "newsmonitor-q2"

version := "1.0"

scalaVersion := "2.11.6"

lazy val akkaVersion = "2.3.12"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "spray nightlies repo" at "http://nightlies.spray.io"
)

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.8.3",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "io.spray" %% "spray-client" % "1.3.1",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.mockito" % "mockito-all" % "1.8.4" % "test"
)
