package be.wegenenverkeer.mosaic.infrastructure

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import be.wegenenverkeer.appstatus.monitor.MonitorActor
import be.wegenenverkeer.funcqrs.{PgEnvelopePublisherFactory, PgPublisherFactory}
import com.softwaremill.tagging._
import io.funcqrs.akka.backend.AkkaBackend

trait AppAkkaModule { akkaModule =>

  def materializer: Materializer

  def actorSystem: ActorSystem

  lazy val publisherFactory = new PgPublisherFactory(actorSystem, materializer)

  lazy val envelopePublisherFactory = new PgEnvelopePublisherFactory(actorSystem, materializer)

  implicit lazy val backend = new AkkaBackend {
    val actorSystem: ActorSystem = akkaModule.actorSystem
  }

}
