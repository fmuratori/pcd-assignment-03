package part2.akka

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.cluster.ddata.GCounterKey
import akka.remote.RemoteTransportException
import com.typesafe.config.ConfigFactory
import DistributedDataActor.Command

object Application {

  def main(args: Array[String]): Unit = {
    /* =============== actors initialization =============== */

    val ports = Seq(25251, 25252, 25253, 0) // with 0 a free unspecified port is picked

    for (port <- ports) {
      try {
        // Override the configuration of the port
        val config = ConfigFactory.parseString(s"""
          akka.remote.artery.canonical.port=$port
          akka.actor.allow-java-serialization = on
          """).withFallback(ConfigFactory.load("application_cluster"))

        // DistributedData actor
        var dataActorRef: ActorRef[Command] = null;
        ActorSystem[Nothing](Behaviors.setup[Nothing] { context =>
          // Create an actor that handles cluster domain events
          dataActorRef = context.spawn(DistributedDataActor(), "DataListener")
          Behaviors.empty
        }, "ClusterSystem", config)

        /* =============== game initialization =============== */
        val n = 2
        val m = 2

        val imagePath = "src/main/resources/bletchley-park-mansion.jpg"

        val puzzle = new PuzzleBoard(n, m, imagePath, dataActorRef)
        puzzle.setVisible(true)

        return

      } catch {
        case _:RemoteTransportException => println("Port " + port + " already in use");
      }
    }
  }
}