name := "learning-akka"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(

  "com.typesafe.akka" %% "akka-actor" % "2.4.17",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.17",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.5",
  "com.typesafe.akka" %% "akka-http" % "10.0.5",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5",
  "org.reactivemongo" %% "reactivemongo" % "0.12.3",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.slf4j" % "slf4j-api" % "1.7.5"

)
