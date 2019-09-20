package examples.udemy.s3_AkkaBasicTools.ActorPaths

import akka.actor.{ActorSystem, PoisonPill, Props}


object ActorPath extends App {

  val system = ActorSystem("actor-paths")

  val counter1 = system.actorOf(Props[Counter], "counter")

  println(s"Actor Reference for counter1 is: ${counter1}")

  val counterSelection1 = system.actorSelection("counter")

  println(s"Actor Selection for counter1 is: ${counterSelection1}")

  counter1 ! PoisonPill

  Thread.sleep(100)

  val counter2 = system.actorOf(Props[Counter], "counter")

  println(s"Actor Reference for count2 is: ${counter2}")

  val counterSelection2 = system.actorSelection("counter")

  println(s"Actor Selection for counter2 is: ${counterSelection2}")

  system.shutdown()
}


object Watch extends App {

  val system = ActorSystem("watch-actor-selection")

  val counter = system.actorOf(Props[Counter], "counter")

  val watcher = system.actorOf(Props[Watcher], "watcher")

  Thread.sleep(1000)

  system.shutdown()
}
