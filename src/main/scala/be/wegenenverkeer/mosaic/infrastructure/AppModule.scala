package be.wegenenverkeer.mosaic.infrastructure

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.BackoffSupervisor
import akka.stream.Materializer
import be.wegenenverkeer.mosaic.api.AppPoAuth
import be.wegenenverkeer.mosaic.domain.service.geowebcache.{GWCInvalidatorActor, GWCSeedActor, GeowebcacheService}
import be.wegenenverkeer.mosaic.domain.service.storage.{EnvelopeStorage, MultipleWritersEnvelopeStorage, S3Bucket, S3EnvelopeStorage}
import com.amazonaws.auth.{AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
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

  lazy val awsCredentialsProvider: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain()

  lazy val envelopeStorage: EnvelopeStorage = {
    val bucketName = configuration
      .getOptional[String]("aws.s3.files.bucket.name")
      .getOrElse(sys.error("aws.s3.files.bucket.name required"))

    val readerPrefix = configuration
      .getOptional[String]("aws.s3.files.bucket.object-prefix.reader")
      .getOrElse(sys.error("aws.s3.files.bucket.object-prefix.reader required"))
    val writerPrefixes = configuration
      .getOptional[String]("aws.s3.files.bucket.object-prefix.writers")
      .getOrElse(sys.error("aws.s3.files.bucket.object-prefix.writers required"))
      .split(",").map(_.trim).filterNot(_.isEmpty)

    def s3bucket(prefix: String) = S3Bucket(bucketName, prefix)

    val reader = new S3EnvelopeStorage(s3bucket(readerPrefix), awsCredentialsProvider)(executionContext, materializer)

    val writers =
      writerPrefixes.map { writerPrefix =>
        new S3EnvelopeStorage(s3bucket(writerPrefix), awsCredentialsProvider)(executionContext, materializer)
      }

    new MultipleWritersEnvelopeStorage(reader, writers)
  }

  val gwcInvalidatorActorSupervisor: ActorRef @@ GWCInvalidatorActor = {
    val gwcInvalidatorActorSupervisorProps =
      BackoffSupervisor.props(
        childProps   = GWCInvalidatorActor.props(envelopeStorage, geowebcacheService),
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


