package examples.udemy.s8_AkkaHttp.ClientApi

import akka.actor.ActorSystem

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.HostConnectionPool
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._


object HostLevel extends App {
  import JsonProtocol._

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  implicit val ec = system.dispatcher

  val poolClientFlow: Flow[(HttpRequest, Int), (Try[HttpResponse], Int), HostConnectionPool] =
    Http().cachedHostConnectionPool[Int]("api.ipify.org")

  val responseFuture: Future[(Try[HttpResponse], Int)] =
    Source.single(HttpRequest(uri = "/?format=json") -> 4).via(poolClientFlow).runWith(Sink.head)

  responseFuture map {
    case (Success(res), _) =>
      res.status match {
        case OK =>
          Unmarshal(res.entity).to[IpInfo].map { info =>
            println(s"The information for my ip is: $info")
            shutdown()
          }
        case _ =>
          Unmarshal(res.entity).to[String].map { body =>
            println(s"The response status is ${res.status} and response body is ${body}")
            shutdown()
          }
      }
    case (Failure(err), i) =>
      println(s"Error happened ${err}")
      shutdown()
  }

  def shutdown() = {
    Http().shutdownAllConnectionPools().onComplete{ _ =>
      system.shutdown()
      system.awaitTermination()
      // Await.result(system.terminate(), 1 seconds)
    }
  }
}
