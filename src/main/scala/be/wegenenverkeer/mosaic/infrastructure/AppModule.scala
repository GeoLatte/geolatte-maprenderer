package be.wegenenverkeer.mosaic.infrastructure

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.BackoffSupervisor
import akka.stream.Materializer
import be.wegenenverkeer.mosaic.api.AppPoAuth
import be.wegenenverkeer.mosaic.domain.service.geowebcache.{GWCInvalidatorActor, GWCSeedActor, GeowebcacheService}
import be.wegenenverkeer.mosaic.domain.service.storage._
import com.softwaremill.macwire._
import com.softwaremill.tagging._
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AppModule {

  def appPoAuth: AppPoAuth

  def materializer: Materializer

  def actorSystem: ActorSystem

  def configuration: Configuration

  implicit def executionContext: ExecutionContext

  lazy val geowebcacheService: GeowebcacheService = wire[GeowebcacheService]

  def envelopeReader: EnvelopeReader

  val gwcInvalidatorActorSupervisor: ActorRef @@ GWCInvalidatorActor = {
    val gwcInvalidatorActorSupervisorProps =
      BackoffSupervisor.props(
        childProps   = GWCInvalidatorActor.props(envelopeReader, geowebcacheService),
        childName    = "gwcInvalidatorActor",
        minBackoff   = 5.seconds, //wacht minstens X om opnieuw te proberen
        maxBackoff   = 5.minutes, //wacht max X om opnieuw te proberen
        randomFactor = 0 // geen random delay nodig om niet allemaal op hetzelfde moment terug te starten
      )

    actorSystem.actorOf(gwcInvalidatorActorSupervisorProps, "gwcInvalidatorActorSupervisor").taggedWith[GWCInvalidatorActor]
  }

  val gwcSeedActor: ActorRef @@ GWCSeedActor =
    actorSystem.actorOf(GWCSeedActor.props(geowebcacheService), "GWCSeedActor").taggedWith[GWCSeedActor]
}
