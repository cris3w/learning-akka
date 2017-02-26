package examples

import akka.actor.{Actor, ActorSystem, Props}


case class WhoToGreet(who: String)


class Greeter extends Actor {

  def receive = {
    case WhoToGreet(who) => println(s"Hello $who")
  }
}


object HelloAkkaScala extends App {

  val system = ActorSystem("Hello-Akka")

  val greeter = system.actorOf(Props[Greeter], "greeter")

  greeter ! WhoToGreet("Akka")
}
