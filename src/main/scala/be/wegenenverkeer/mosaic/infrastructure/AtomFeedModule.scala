package be.wegenenverkeer.mosaic.infrastructure

import akka.actor.ActorSystem
import be.wegenenverkeer.atomium.api._
import be.wegenenverkeer.atomium.format.Url
import be.wegenenverkeer.atomium.store.{JdbcEventStoreMetadata, PostgresEventStore}
import be.wegenenverkeer.mosaic.infrastructure.atom.Indexing.PostgresIndexingActor
import be.wegenenverkeer.mosaic.infrastructure.atom.{Indexing, Slick3AwareFeedService}
import be.wegenenverkeer.slick3.DbRunner
import com.softwaremill.tagging._
import play.api.http.MimeTypes
import play.api.libs.json.{Format, Json}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AtomFeedModule {

  def feedBaseUrl: Url

  implicit def dbRunner: DbRunner

  implicit def executionContext: ExecutionContext

  def actorSystem: ActorSystem

  private def createStore[T](feedName: String, format: Format[T]): PostgresEventStore[T] = {
    val entriesTableName     = s"feed_entries_$feedName"
    val idColumnName         = "uuid"
    val updatedColumnName    = "timestamp"
    val primaryKeyColumnName = "id"
    val sequenceNoColumnName = "sequence"
    val entryValColumnName   = "value"

    val store =
      new PostgresEventStore[T](
        new JdbcEventStoreMetadata(
          entriesTableName,
          idColumnName,
          updatedColumnName,
          primaryKeyColumnName,
          sequenceNoColumnName,
          entryValColumnName
        ),
        new Codec[T, String] {
          override def encode(value: T): String   = Json.toJson[T](value)(format).toString()
          override def decode(encoded: String): T = Json.parse(encoded).as[T](format)
          override def getMimeType: String        = MimeTypes.JSON
        }
      )

    store
  }

  private def createMetadata(feedName: String) = {
    val pageSize = 100

    val url   = feedBaseUrl.add("rest").add("feeds").add(feedName)

    new FeedMetadata(pageSize, url.getPath, feedName)
  }
/*
  lazy val organisatieFeedService: Slick3AwareFeedService[OrganisatieFeedEvent] = {
    val feedNaam = "organisatie"

    val organisatieFeedStore =
      createStore[OrganisatieFeedEvent](
        feedNaam,
        OrganisatieFeedEvents.organisatieFeedEventFormats
      )

    val indexerCfg = Indexing.Config(
      marker             = feedNaam,
      maxReindexInterval = 1.minute
    )

    val indexer = actorSystem.actorOf(
      props = Indexing.PostgresIndexingActor.props(organisatieFeedStore, indexerCfg),
      name  = feedNaam + "Indexer"
    ).taggedWith[PostgresIndexingActor]

    new Slick3AwareFeedService(organisatieFeedStore, createMetadata(feedNaam), indexer)
  }
*/
}