package be.wegenenverkeer.mosaic.infrastructure

import java.time.{Duration => JavaDuration}

import _root_.slick.jdbc.JdbcBackend.DatabaseDef
import be.wegenenverkeer.atomium.extension.feedconsumer.{FeedPosition, FeedPositionRepo}
import be.wegenenverkeer.mosaic.api.AppPoAuth
import be.wegenenverkeer.mosaic.domain.service.{DataloaderService, VerkeersbordenService}
import be.wegenenverkeer.slick3._
import com.softwaremill.macwire._
import org.ehcache.CacheManager
import org.ehcache.config.builders.{CacheConfigurationBuilder, CacheManagerBuilder, ExpiryPolicyBuilder, ResourcePoolsBuilder}
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

  lazy val feedPositionRepo: FeedPositionRepo = new FeedPositionRepo(dbRunner)

  // we zijn genoodzaakt om dezelfde eh cache versie te gebruiken als geolatte, anders krijgen we conflicten
  lazy val cacheManager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder
    .withCache(
      "verkeersbordenFeedPage",
      CacheConfigurationBuilder
        .newCacheConfigurationBuilder(classOf[String], classOf[String], ResourcePoolsBuilder.heap(2))
        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(JavaDuration.ofSeconds(10)))
        .build
    )
    .withCache(
      "dataloaderFeedPosition",
      CacheConfigurationBuilder
        .newCacheConfigurationBuilder(classOf[String], classOf[FeedPosition], ResourcePoolsBuilder.heap(1))
        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(JavaDuration.ofSeconds(5)))
        .build
    )
    .build(true)

  lazy val dataloaderService: DataloaderService = wire[DataloaderService]
  lazy val verkeersbordenService: VerkeersbordenService = wire[VerkeersbordenService]


}
