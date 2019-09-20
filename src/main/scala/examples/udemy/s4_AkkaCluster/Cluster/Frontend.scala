package examples.udemy.s4_AkkaCluster.Cluster

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import com.typesafe.config.ConfigFactory
import examples.udemy.s4_AkkaCluster.Commos._

import scala.util.Random


class Frontend extends Actor {

  var backends: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]

  def receive: Receive = {
    case Add if backends.isEmpty =>
      println("Service unavailable, cluster doesn't have backend node")
    case addOp: Add =>
      println("Frontend: I'll forward add operation to backend node to handle it")
      backends(Random.nextInt(backends.size)) forward addOp
    case BackendRegistration if !backends.contains(sender) =>
      backends = backends :+ sender()
      context watch sender
    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }
}


object Frontend {

  private var _frontend: ActorRef = _

  def initiate(): Unit = {
    val config = ConfigFactory.load().getConfig("Frontend")

    val system = ActorSystem("ClusterSystem", config)

    _frontend = system.actorOf(Props[Frontend], "frontend")
  }

  def getFrontend: ActorRef = _frontend
}
