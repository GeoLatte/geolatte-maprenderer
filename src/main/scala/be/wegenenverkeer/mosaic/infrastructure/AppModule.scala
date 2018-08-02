package be.wegenenverkeer.mosaic.infrastructure

import _root_.slick.jdbc.JdbcBackend.DatabaseDef
import be.wegenenverkeer.mosaic.api.AppPoAuth
import be.wegenenverkeer.slick3._
import io.funcqrs.config.Api.projection
import io.funcqrs.config.api._
import play.api.Configuration

/**
 * In de domain module mogen enkel "pure" domain deps komen
 * die niet afhankelijk zijn van runtimes en frameworks zoals Play en Akka.
 *
 */
trait AppDomainModule {

  def dbRunner: DbRunner

  def db: DatabaseDef
}

trait AppModule extends AppDomainModule with AppAkkaModule {

//  backend.configure {
//    aggregate((id: MachtigingId) => Machtiging.behavior(id, toepassingViewRepo))
//      .withName("MachitigingAggregateActor")
//  }
//
//  backend.configure {
//    projection(
//      projection       = machtigingViewFunCqrsProjection,
//      publisherFactory = envelopePublisherFactory.publisherForEvent[ThorEvent](MachtigingProtocol.tag, ToepassingProtocol.tag),
//      name             = MachtigingViewProjection.naam
//    ).withCustomOffsetPersistence(new OffsetRepoOffsetPersistenceStrategy(machtigingViewOffsetRepo))
//  }

  def appPoAuth: AppPoAuth

  def configuration: Configuration

}
