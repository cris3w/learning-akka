package examples.akkabasictools.routing

import akka.actor.{Actor, ActorRef, Props}
import Worker._


class Router extends Actor {

  var routees: List[ActorRef] = _

  override def preStart() = {
    routees = List.fill(5)(context.actorOf(Props[Worker]))
  }

  def receive() = {
    case msg: Work =>
      println("I'm a Router and I received a Message .....")
      routees(util.Random.nextInt(routees.size)) forward msg
  }
}


class RouterGroup(routees: List[String]) extends Actor {

  def receive = {
    case msg: Work =>
      println("I'm a Router Group and I received Work Message .....")
      context.actorSelection(routees(util.Random.nextInt(routees.size))) forward msg
  }
}
