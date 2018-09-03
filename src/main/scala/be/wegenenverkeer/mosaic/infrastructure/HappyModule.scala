package be.wegenenverkeer.mosaic.infrastructure

import _root_.slick.jdbc.JdbcBackend.DatabaseDef
import akka.actor.{ActorRef, ActorSystem}
import be.wegenenverkeer.metrics.MetricsHolder
import be.wegenenverkeer.mosaic.domain.service.geowebcache.{GWCInvalidatorActor, GWCSeedActor}
import be.wegenenverkeer.mosaic.domain.service.storage.EnvelopeReader
import be.wegenenverkeer.mosaic.domain.service.{DataloaderService, VerkeersbordenService}
import be.wegenenverkeer.mosaic.infrastructure.happy.HappyRegistrar
import com.codahale.metrics.MetricRegistry
import com.softwaremill.macwire.wire
import com.softwaremill.tagging.@@
import play.api.Application

trait HappyModule extends be.wegenenverkeer.appstatus.support.HappyModule {

  val metricRegistry: MetricRegistry = MetricsHolder.metricRegistry

  def application: Application

  def actorSystem: ActorSystem

  def dbOpt: Option[DatabaseDef]

  def dataloaderServiceOpt: Option[DataloaderService]

  def verkeersbordenServiceOpt: Option[VerkeersbordenService]

  def envelopeReader: EnvelopeReader

  def gwcInvalidatorActorSupervisor: ActorRef @@ GWCInvalidatorActor

  def gwcSeedActor: ActorRef @@ GWCSeedActor

  // Initialiseert de happy page
  wire[HappyRegistrar]

}
