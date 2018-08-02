package be.wegenenverkeer.mosaic.infrastructure

import _root_.slick.jdbc.JdbcBackend.DatabaseDef
import akka.actor.ActorSystem
import be.wegenenverkeer.mosaic.infrastructure.happy.HappyRegistrar
import be.wegenenverkeer.metrics.MetricsHolder
import com.codahale.metrics.MetricRegistry
import com.softwaremill.macwire.wire
import play.api.Application

trait HappyModule extends be.wegenenverkeer.appstatus.support.HappyModule {

  val metricRegistry: MetricRegistry = MetricsHolder.metricRegistry

  def application: Application

  def db: DatabaseDef

  def actorSystem: ActorSystem

  // Initialiseert de happy page
  wire[HappyRegistrar]

}
