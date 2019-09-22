package examples.udemy.s4_AkkaPersistence

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence._


object Counter {

  sealed trait Operation {
    val count: Int
  }

  case class Increment(override val count: Int) extends Operation
  case class Decrement(override val count: Int) extends Operation

  case class Cmd(op: Operation) // Commands represent operations from the outside world
  case class Evt(op: Operation) // Events represent stored operations from the Journal

  case class State(count: Int)
}


class Counter extends PersistentActor with ActorLogging {
  import Counter._

  // Every message in the Journal needs an Id for its persistence entity
  // The persistence Id should be unique
  override def persistenceId: String = "counter-example"

  var state: State = State(count = 0)

  // A persistence actor starts with recovering mood
  override def receiveRecover: Receive = {
    case evt: Evt =>
      println(s"Receiving $evt on recovering mood")
      updateState(evt)
    case SnapshotOffer(_, snapshot: State) =>
      println(s"Receiving snapshot $snapshot on recovering mood")
      state = snapshot
    case RecoveryCompleted =>
      println(s"Recovery completed, It'll switch to receiving mode")
  }

  override def receiveCommand: Receive = {
    case cmd @ Cmd(op) =>
      println(s"Receiving $cmd")
      persist(Evt(op)) { evt =>
        updateState(evt)
      }

    case "print" =>
      println(s"The current state of counter is $state")

    case _: SaveSnapshotSuccess =>
      println(s"Snapshot saving succeed")
    case SaveSnapshotFailure(_, reason) =>
      println(s"Snapshot saving failed for $reason")
  }

  def updateState(evt: Evt): Unit =
    evt match {
      case Evt(Increment(count)) =>
        state = State(count = state.count + count)
        takeSnapshot()
      case Evt(Decrement(count)) =>
        state = State(count = state.count - count)
        takeSnapshot()
    }

  def takeSnapshot(): Unit =
    if (state.count % 5 == 0)
      saveSnapshot(state)
}


object Persistence extends App {
  import Counter._

  val system = ActorSystem("persistent-actors")

  val counter = system.actorOf(Props[Counter])

  counter ! Cmd(Increment(3))
  counter ! Cmd(Increment(5))
  counter ! Cmd(Decrement(3))

  counter ! "print"

  Thread.sleep(1000)

  system.terminate()
}
