package be.wegenenverkeer.mosaic.infrastructure

import akka.actor.ActorSystem
import be.wegenenverkeer.atomium.client.ImplicitConversions._
import be.wegenenverkeer.atomium.extension.feedconsumer.{FeedConsumer, FeedConsumerListener, FeedPosition, FeedPositionRepo}
import be.wegenenverkeer.atomium.japi.client.{AtomiumClient, FeedEntry}
import be.wegenenverkeer.mosaic.domain.service.VerkeersbordenService
import be.wegenenverkeer.mosaic.domain.service.atomium.VerkeersbordenChangeEventConsumer
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

  val verkeersbordenService: VerkeersbordenService = wire[VerkeersbordenService]

  val verkeersbordenChangeEventConsumer: VerkeersbordenChangeEventConsumer = wire[VerkeersbordenChangeEventConsumer]

  def feedPositionRepo: FeedPositionRepo

  val verkeersbordenFeedConsumer = new FeedConsumer(
    atomiumClient       = wdbAtomiumClient,
    feedPositionRepo    = feedPositionRepo,
    feedNaam            = "verkeersborden",
    feedUrl             = "/rest/events/zi/verkeersborden/feed",
    pollInterval        = 30.seconds,
    maxConsumeTime      = 60.seconds,
    changeEventConsumer = verkeersbordenChangeEventConsumer
  )(dbRunner, actorSystem.dispatcher)

  verkeersbordenFeedConsumer.start(new FeedConsumerListener with Logging {
    override def error(error: Throwable, feedEntry: Option[FeedEntry[JsonNode]]) = {
      logger.error(s"Fout bij verwerken vkb feed: ${feedEntry.map(_.getEntry)}", error)
    }

    override def processedEntry(processed: FeedEntry[JsonNode]) =
      logger.debug(s"processed ${processed.getSelfHref} ${processed.getEntry.getId}")

    override def startedListening(feedPosition: Option[FeedPosition]) = {
      logger.debug(s"startedListening at $feedPosition")
    }
  })

}
