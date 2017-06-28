package examples.akkahttp.restapi

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import examples.akkahttp.restapi.db.TweetManager
import examples.akkahttp.restapi.model.{Tweet, TweetEntity}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import spray.json._

import scala.concurrent.ExecutionContext


trait RestApi {
  import examples.akkahttp.restapi.model.TweetProtocol._
  import examples.akkahttp.restapi.model.TweetEntity._
  import examples.akkahttp.restapi.model.TweetEntityProtocol.EntityFormat

  implicit val system: ActorSystem
  implicit val materializer: Materializer
  implicit val ec: ExecutionContext

  val route: Route =
    pathPrefix("tweets") {
      // POST http://localhost:9000/tweets
      (post & entity(as[Tweet])) { tweet =>
        complete {
          TweetManager.save(tweet) map { r =>
            Created -> Map("id" -> r.id).toJson
          }
        }
      } ~
        // GET http://localhost:9000/tweets/{id}
        (get & path(Segment)) { id =>
          complete {
            TweetManager.findById(id) map { t =>
              OK -> t
            }
          }
        } ~
        // DELETE http://localhost:9000/tweets/{id}
        (delete & path(Segment)) { id =>
          complete {
            TweetManager.deleteById(id) map { _ =>
              NoContent
            }
          }
        } ~
        // GET http://localhost:9000/tweets
        get {
          complete {
            TweetManager.find map { ts =>
              OK -> ts.map(_.as[TweetEntity])
            }
          }
        }
    }
}


object Api extends App with RestApi {

  override implicit val system = ActorSystem("rest-api")
  override implicit val materializer = ActorMaterializer()
  override implicit val ec = system.dispatcher

  val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)

  println(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
  Console.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.shutdown())
}
