package examples.udemy.s4_AkkaCluster.LoadBalancer

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import examples.udemy.s4_AkkaCluster.Commos.Add


class Backend extends Actor {

  def receive: Receive = {
    case Add(_, _) =>
      println(s"I'm a backend with path: $self and I received add operation")
  }
}


object Backend {

  def initiate(port: Int): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
      .withFallback(ConfigFactory.load("loadbalancer"))

    val system = ActorSystem("ClusterSystem", config)

    val backend = system.actorOf(Props[Backend], "backend")
  }
}
