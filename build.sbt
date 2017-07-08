
name := "learning-akka"

version := "1.0"

scalaVersion := "2.12.1"


libraryDependencies ++= Seq(

  "com.typesafe.akka" %% "akka-actor" % "2.4.17",

  // 4 - akka persistence
  "com.typesafe.akka" % "akka-persistence_2.12" % "2.4.17",
  "org.iq80.leveldb" % "leveldb" % "0.9",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",

  // 5 - akka cluster
  "com.typesafe.akka" %% "akka-remote" % "2.4.17",

  // 6 - testing actors
  "org.scalactic" % "scalactic_2.12" % "3.0.3",
  "org.scalatest" % "scalatest_2.12" % "3.0.3" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.5" % Test,

  // 8 - akka http
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.17",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.5",
  "com.typesafe.akka" %% "akka-http" % "10.0.5",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5",
  "org.reactivemongo" %% "reactivemongo" % "0.12.3",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.slf4j" % "slf4j-api" % "1.7.5"

)
