package be.wegenenverkeer.mosaic.infrastructure

import akka.actor.ActorSystem
import be.wegenenverkeer.atomium.client.ImplicitConversions._
import be.wegenenverkeer.atomium.extension.feedconsumer._
import be.wegenenverkeer.atomium.japi.client.{AtomiumClient, FeedEntry}
import be.wegenenverkeer.mosaic.domain.service.atomium.VerkeersbordenChangeEventConsumer
import be.wegenenverkeer.mosaic.domain.service.{DataloaderService, EnvelopeStorage, FileEnvelopeStorage, VerkeersbordenService}
import be.wegenenverkeer.mosaic.util.Logging
import be.wegenenverkeer.slick3.DbRunner
import com.fasterxml.jackson.databind.JsonNode
import com.softwaremill.macwire._
import io.funcqrs.akka.backend.AkkaBackend
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AppAtomium {

  def configuration: Configuration

  def actorSystem: ActorSystem

  def backend: AkkaBackend

  def dbRunner: DbRunner

  def dataloaderService: DataloaderService

  implicit def executionContext: ExecutionContext

  lazy val wdbAtomiumClient = {
    val baseUrl =
      configuration.getOptional[String]("verkeersborden.url").getOrElse(sys.error("Heb een waarde nodig voor verkeersborden.url"))

    new AtomiumClient.Builder()
      .setBaseUrl(baseUrl)
      .setAccept("application/vnd.awv.wdb-v2.0+json")
      .setConnectTimeout(5000)
      .setMaxConnections(2)
      .build()
      .asScala
  }

  def verkeersbordenService: VerkeersbordenService

  def envelopeStorage: EnvelopeStorage

  lazy val verkeersbordenChangeEventConsumer: VerkeersbordenChangeEventConsumer = wire[VerkeersbordenChangeEventConsumer]

  def feedPositionRepo: FeedPositionRepo

  {
    val feedUrl = "/rest/events/zi/verkeersborden/feed"

    def startInitieelOpDataloaderPositieFeedPositionRepo = {
      import SlickPgProfile.api._
      new StartInitieelOpCustomPositieFeedPositionRepo(() => DBIO.from(dataloaderService.getVerkeersbordenFeedPosition(feedUrl)), dbRunner)
    }

    // na deze periode beginnen we fouten te loggen als dataloader niet aan het einde zit en niet vooruit gaat op de verkeersborden feed
    val maxConsumeTime = 30.minutes

    val verkeersbordenFeedConsumer: FeedConsumer = new FeedConsumer(
      atomiumClient       = wdbAtomiumClient,
      feedPositionRepo    = startInitieelOpDataloaderPositieFeedPositionRepo,
      feedNaam            = "verkeersborden",
      feedUrl             = feedUrl,
      pollInterval        = 30.seconds,
      maxConsumeTime      = maxConsumeTime,
      changeEventConsumer = verkeersbordenChangeEventConsumer
    )(dbRunner, actorSystem.dispatcher)

    val retryInterval = 10.seconds

    val progressLimiter = new DataloaderProgressLimiter(
      dataloaderService,
      feedUrl,
      verkeersbordenService,
      retryInterval = retryInterval,
      maxRetries    = maxConsumeTime.div(retryInterval + 1.second).toInt
    )

    val feedConsumerListener = new FeedConsumerListener with Logging {
      override def error(error: Throwable, feedEntry: Option[FeedEntry[JsonNode]]): Unit = {
        logger.error(s"Fout bij verwerken vkb feed: ${feedEntry.map(_.getEntry)}", error)
      }

      override def processedEntry(processed: FeedEntry[JsonNode]): Unit =
        logger.debug(s"processed ${processed.getSelfHref} ${processed.getEntry.getId}")

      override def startedListening(feedPosition: Option[FeedPosition]): Unit = {
        logger.debug(s"startedListening at $feedPosition")
      }
    }

    verkeersbordenFeedConsumer.start(feedConsumerListener, progressLimiter)
  }

}
