package examples.spikes.routewoask

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.{Future, Promise}
import scala.io.StdIn
import scala.util.{Failure, Success}


case object Hello

class Hello(origin: ActorRef) extends Actor {

  def receive: Receive = {
    case Hello =>
      origin ! "<h1>Say hello to akka-http</h1>"
  }
}


object WebServer extends App {

  implicit val system = ActorSystem("spike-route-wo-ask")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val p = Promise[String]
  val origin = system.actorOf(Props(new Actor {
    def receive: Receive = {
      case msg: String =>
        println("origin")
        val f = Future.successful(msg)
        p.completeWith(f)
    }
  }))

  val hello = system.actorOf(Props(new Hello(origin)), "Hello")

  val route =
    path("hello") {
      get {
        hello ! Hello
        onComplete(p.future) {
          case Success(msg) =>
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, msg))
          case Failure(_) =>
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Error</h1>"))
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)

  println("Server online at http://localhost:9000/")
  println("Press RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
  
}
