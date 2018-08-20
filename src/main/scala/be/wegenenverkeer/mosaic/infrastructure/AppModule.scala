package be.wegenenverkeer.mosaic.infrastructure

import java.time.{Duration => JavaDuration}

import _root_.slick.jdbc.JdbcBackend.DatabaseDef
import akka.pattern.BackoffSupervisor
import be.wegenenverkeer.atomium.extension.feedconsumer.{FeedPosition, FeedPositionRepo}
import be.wegenenverkeer.mosaic.api.AppPoAuth
import be.wegenenverkeer.mosaic.domain.service._
import be.wegenenverkeer.mosaic.domain.service.geowebcache.{GWCInvalidatorActor, GeowebcacheService}
import be.wegenenverkeer.mosaic.domain.service.storage.{EnvelopeStorage, S3Bucket, S3EnvelopeStorage}
import be.wegenenverkeer.slick3._
import com.amazonaws.auth.{AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.softwaremill.macwire._
import org.ehcache.CacheManager
import org.ehcache.config.builders.{CacheConfigurationBuilder, CacheManagerBuilder, ExpiryPolicyBuilder, ResourcePoolsBuilder}
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

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

  def appPoAuth: AppPoAuth

  def configuration: Configuration

  implicit def executionContext: ExecutionContext

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

  lazy val dataloaderService: DataloaderService         = wire[DataloaderService]
  lazy val verkeersbordenService: VerkeersbordenService = wire[VerkeersbordenService]
  lazy val geowebcacheService: GeowebcacheService       = wire[GeowebcacheService]

  lazy val awsCredentialsProvider: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain()

  lazy val envelopeStorage: EnvelopeStorage = {
    val s3bucket = S3Bucket(
      configuration
        .getOptional[String]("aws.s3.files.bucket.name")
        .getOrElse(sys.error("aws.s3.files.bucket.name required")),
      configuration.getOptional[String]("aws.s3.files.bucket.object-prefix")
        .getOrElse(sys.error("aws.s3.files.bucket.object-prefix required")),
    )
    new S3EnvelopeStorage(s3bucket, awsCredentialsProvider)(executionContext, materializer)
  }

  {
    val gwcInvalidatorActorSupervisorProps =
      BackoffSupervisor.props(
        childProps   = GWCInvalidatorActor.props(envelopeStorage, geowebcacheService),
        childName    = "gwcInvalidatorActor",
        minBackoff   = 5.seconds, //wacht minstens X om opnieuw te proberen
        maxBackoff   = 5.minutes, //wacht max X om opnieuw te proberen
        randomFactor = 0 // geen random delay nodig om niet allemaal op hetzelfde moment terug te starten
      )

    actorSystem.actorOf(gwcInvalidatorActorSupervisorProps, "gwcInvalidatorActorSupervisor")
  }

}
