package examples.udemy.s3_AkkaBasicTools.Behavior

import akka.actor.{Actor, ActorSystem, Props, Stash}


object UserStorage {

  trait DBOperation
  object DBOperation {

    case object Create extends DBOperation
    case object Update extends DBOperation
    case object Read extends DBOperation
    case object Delete extends DBOperation
  }

  case object Connect
  case object Disconnect
  case class Operation(op: DBOperation, user: Option[User])

  case class User(username: String, email: String)
}


class UserStorage extends Actor with Stash {
  import UserStorage._

  def receive = disconnected

  def connected: Actor.Receive = {
    case Disconnect =>
      println("User Storage disconnected from DB")
      context.unbecome()
    case Operation(op, user) =>
      println(s"User Storage receive ${op} to do in user: ${user}")
  }

  def disconnected: Actor.Receive = {
    case Connect =>
      println("User Storage connected to DB")
      unstashAll()
      context.become(connected)
    case _ =>
      stash()
  }
}


object BecomeHotswap extends App {
  import UserStorage._

  val system = ActorSystem("Hotswap-Become")

  val userStorage = system.actorOf(Props[UserStorage], "userStorage")

  userStorage ! Operation(DBOperation.Create, Some(User("Admin", "admin@packt.com")))
  userStorage ! Connect
  userStorage ! Disconnect

  Thread.sleep(100)

  system.shutdown()
}
