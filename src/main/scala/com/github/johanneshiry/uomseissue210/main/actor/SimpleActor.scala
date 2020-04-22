package com.github.johanneshiry.uomseissue210.main.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.github.johanneshiry.uomseissue210.main.Main.DummyData
import javax.measure.quantity.{ElectricConductance, ElectricResistance}


object SimpleActor {
  def props(dummyData: Vector[DummyData]): Props = Props(new SimpleActor(dummyData))
}

class SimpleActor(dummyData: Vector[DummyData]) extends Actor with ActorLogging {
  override def preStart(): Unit = log.info(s"SimpleActor ${self.path.name} started")

  override def postStop(): Unit = log.info(s"SimpleActor ${self.path.name} stopped")

  // No need to handle any messages
  override def receive: Receive = {
    case "Run!" =>
      try {
        dummyData.foreach(dummyData => {

          val (r, x, g, b) = (
            dummyData.r.multiply(dummyData.length).asType(classOf[ElectricResistance]),
            dummyData.x.multiply(dummyData.length).asType(classOf[ElectricResistance]),
            dummyData.g.multiply(dummyData.length).asType(classOf[ElectricConductance]),
            dummyData.b.multiply(dummyData.length).asType(classOf[ElectricConductance])
          )

        })
      } catch {
        case e: Exception =>
          sender ! "Done!" // reply to the sender that we are done, to let the future terminate
          log.error("Exception occurred during 'asType()': " + e) // log the exception to collect it in the logs
          throw e // rethrow the exception for debugging
      }
      sender ! "Done!"

    case unknown => log.warning(s"Received unknown command: '$unknown'")
  }

}
