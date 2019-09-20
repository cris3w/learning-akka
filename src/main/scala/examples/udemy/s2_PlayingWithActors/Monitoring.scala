package examples.udemy.s2_PlayingWithActors

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}


class Ares(athena: ActorRef) extends Actor {

  override def preStart() = {
    context.watch(athena)
  }

  override def postStop() = {
    println("Ares postStop .....")
  }

  def receive = {
    case Terminated(_) =>
      println(s"Ares received Terminated")
      context.stop(self)
  }
}


class Athena extends Actor {

  def receive = {
    case msg =>
      println(s"Athena received ${msg}")
      context.stop(self)
  }
}


object Monitoring extends App {

  // Create the 'monitoring' actor system
  val system = ActorSystem("monitoring")

  val athena = system.actorOf(Props[Athena], "Athena")

  val ares = system.actorOf(Props(classOf[Ares], athena), "ares")

  athena ! "Hi"

  Thread.sleep(500)

  system.shutdown()
}
