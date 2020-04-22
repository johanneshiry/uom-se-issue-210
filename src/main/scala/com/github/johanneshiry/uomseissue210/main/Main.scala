package com.github.johanneshiry.uomseissue210.main


import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, CoordinatedShutdown}
import com.github.johanneshiry.uomseissue210.main.actor.SimpleActor
import com.github.johanneshiry.uomseissue210.quantities.PowerSystemUnits
import com.github.johanneshiry.uomseissue210.quantities.interfaces.{SpecificConductance, SpecificResistance}
import javax.measure.Quantity
import javax.measure.quantity.Length
import tec.uom.se.quantity.Quantities

import scala.concurrent.{Await, Future}
import akka.pattern.ask
import akka.util.{Timeout => AkkaTimeout}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random


object Main extends LazyLogging {

  final case class DummyData(r: Quantity[SpecificResistance], x: Quantity[SpecificResistance],
                             g: Quantity[SpecificConductance], b: Quantity[SpecificConductance], length: Quantity[Length])

  def main(args: Array[String]): Unit = {

    // configuration parameters
    val noOfActors = 20
    val maxNoOfDummyDataPerActor = 7000 // maximum of dummy data per actor

    // run
    run(noOfActors, maxNoOfDummyDataPerActor)

    // run multiple loops with varying noOfDummyDataPerActor -> actually NOT reveal the issue ...
    // val startNoOfDummyDataPerActor = 500 // initial amount of dummy data per actor
    // val stepSize = 500 // increase noOfDummyDataPerActor per run by this step size
    // runWithVaryingDummyData(noOfActors, startNoOfDummyDataPerActor, maxNoOfDummyDataPerActor, stepSize)

  }

  def runWithVaryingDummyData(noOfActors: Int, startNoOfDummyDataPerActor: Int, maxNoOfDummyDataPerActor: Int, stepSize: Int): Unit = {

    var currentNoOfDummyData = startNoOfDummyDataPerActor
    while (currentNoOfDummyData <= maxNoOfDummyDataPerActor) {

      logger.info(s"Starting run with noOfDummyData $currentNoOfDummyData")

      run(noOfActors, currentNoOfDummyData)

      logger.info(s"Finished run with noOfDummyData $currentNoOfDummyData")
      currentNoOfDummyData = currentNoOfDummyData + stepSize
    }

  }

  def run(noOfActors: Int, noOfDummyDataPerActor: Int): Unit = {

    // future timeout for akka actors
    implicit val timeout: AkkaTimeout = AkkaTimeout(100, TimeUnit.SECONDS)

    // build the actor system
    val actorSystem = ActorSystem("testSystem")

    // build the actors that should process the dummy data
    val actors = Vector.range(0, noOfActors).map(actorId => actorSystem.actorOf(SimpleActor.props(createTestData(noOfDummyDataPerActor)), "Actor" + actorId))

    // let the actors do their job and wait until they're ready
    Await.ready(
      Future.sequence(actors.map(actor => actor ? "Run!")),
      Duration(50, TimeUnit.SECONDS)
    )

    // shutdown the actor system
    CoordinatedShutdown(actorSystem).run(CoordinatedShutdown.JvmExitReason)

  }

  def createTestData(noOfElements: Int): Vector[DummyData] = {
    Vector.range(0, noOfElements).map(_ => {

      val r = Quantities.getQuantity(Random.nextDouble(), PowerSystemUnits.OHM_PER_KILOMETRE)
      val x = Quantities.getQuantity(Random.nextDouble(), PowerSystemUnits.OHM_PER_KILOMETRE)
      val g = Quantities.getQuantity(Random.nextDouble(), PowerSystemUnits.SIEMENS_PER_KILOMETRE)
      val b = Quantities.getQuantity(Random.nextDouble(), PowerSystemUnits.SIEMENS_PER_KILOMETRE)
      val length = Quantities.getQuantity(Random.nextDouble(), PowerSystemUnits.KILOMETRE)

      DummyData(r, x, g, b, length)
    })
  }

}
