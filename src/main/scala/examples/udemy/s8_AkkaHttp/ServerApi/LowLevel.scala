package examples.udemy.s8_AkkaHttp.ServerApi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{IncomingConnection, ServerBinding}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import scala.concurrent.Future
import scala.io.StdIn


object LowLevel extends App {

  implicit val system = ActorSystem("my-system")

  implicit val materializer = ActorMaterializer()

  implicit val ec = system.dispatcher

  val serverSource: Source[IncomingConnection, Future[ServerBinding]] =
    Http().bind(interface = "localhost", port = 8888)

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)
      connection handleWithSyncHandler(requestHandler)
    }).run()

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(entity = HttpEntity("Hello Akka HTTP Server Side API - Low Level!"))
    case _ =>
      HttpResponse(404, entity = "Unknown resource!")
  }

  println(s"Server online at http://localhost:8888/\nPress RETURN to stop .....")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.shutdown()) // and shutdown when done
}
