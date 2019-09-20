package examples.udemy.s4_AkkaCluster.LoadBalancer

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.cluster.Cluster
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import examples.udemy.s4_AkkaCluster.Commos.Add

import scala.util.Random
import scala.concurrent.duration._


class Frontend extends Actor {
  import context.dispatcher

  var backends: ActorRef = context.actorOf(FromConfig.props, "backendRouter")

  context.system.scheduler.schedule(3.seconds, 3.seconds, self,
    Add(Random.nextInt(100), Random.nextInt(100)))

  def receive: Receive = {
    case addOp: Add =>
      println("Frontend: I'll forward add operation to backend node to handle it")
      backends forward addOp
  }
}


object Frontend {

  private var _frontend: ActorRef = _

  val upToN = 200

  def initiate(): Unit = {
    val config = ConfigFactory.parseString("akka.cluster.roles = [frontend]")
      .withFallback(ConfigFactory.load("loadbalancer"))

    val system = ActorSystem("ClusterSystem", config)
    system.log.info("Frontend will start when 2 backend members in the cluster")

    // registerOnUp
    Cluster(system) registerOnMemberUp {
      _frontend = system.actorOf(Props[Frontend], "frontend")
    }
    // registerOnUp
  }

  def getFrontend: ActorRef = _frontend
}
