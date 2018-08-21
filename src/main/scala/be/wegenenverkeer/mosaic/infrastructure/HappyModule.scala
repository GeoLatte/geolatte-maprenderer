package be.wegenenverkeer.mosaic.infrastructure

import _root_.slick.jdbc.JdbcBackend.DatabaseDef
import akka.actor.{ActorRef, ActorSystem}
import be.wegenenverkeer.mosaic.infrastructure.happy.HappyRegistrar
import be.wegenenverkeer.metrics.MetricsHolder
import be.wegenenverkeer.mosaic.domain.service.geowebcache.GWCInvalidatorActor
import be.wegenenverkeer.mosaic.domain.service.storage.EnvelopeStorage
import be.wegenenverkeer.mosaic.domain.service.{DataloaderService, VerkeersbordenService}
import com.codahale.metrics.MetricRegistry
import com.softwaremill.macwire.wire
import com.softwaremill.tagging.@@
import play.api.Application

trait HappyModule extends be.wegenenverkeer.appstatus.support.HappyModule {

  val metricRegistry: MetricRegistry = MetricsHolder.metricRegistry

  def application: Application

  def db: DatabaseDef

  def actorSystem: ActorSystem

  def dataloaderService: DataloaderService

  def verkeersbordenService: VerkeersbordenService

  def envelopeStorage: EnvelopeStorage

  def gwcInvalidatorActorSupervisor: ActorRef @@ GWCInvalidatorActor

  // Initialiseert de happy page
  wire[HappyRegistrar]

}
