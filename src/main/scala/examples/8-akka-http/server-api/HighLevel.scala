package examples

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import scala.concurrent.Future
import scala.io.StdIn


object HighLevel extends App {

  implicit val system = ActorSystem("my-system")

  implicit val materializer = ActorMaterializer()

  implicit val ec = system.dispatcher

  val route =
    path(""){
      get {
        complete("Hello Akka HTTP Server Side API - High Level!")
      }
    }

  val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(route, "localhost", 8888)

  println(s"Server online at http://localhost:8888/\nPress RETURN to stop .....")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
