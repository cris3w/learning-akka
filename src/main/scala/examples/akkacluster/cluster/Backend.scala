package examples.akkacluster.cluster

import akka.actor.{Actor, ActorSystem, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import com.typesafe.config.ConfigFactory
import examples.akkacluster.commons._


class Backend extends Actor {

  val cluster: Cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  def receive: Receive = {
    case Add(_, _) =>
      println(s"I'm a backend with path: $self and I received add operation")
    case MemberUp(member) =>
      if (member.hasRole("frontend")) {
        context.actorSelection(RootActorPath(member.address) / "user" / "frontend") ! BackendRegistration
      }
  }
}


object Backend {

  def initiate(port: Int): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.load().getConfig("Backend"))

    val system = ActorSystem("ClusterSystem", config)

    val backend = system.actorOf(Props[Backend], "backend")
  }
}
