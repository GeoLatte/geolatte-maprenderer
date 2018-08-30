package be.wegenenverkeer.mosaic.infrastructure.atom

import java.time.OffsetDateTime
import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ReceiveTimeout}
import akka.pattern.pipe
import be.wegenenverkeer.atomium.api._
import be.wegenenverkeer.atomium.store.PostgresEventStore
import be.wegenenverkeer.slick3._
import be.wegenenverkeer.mosaic.infrastructure.SlickPgProfile.api._
import be.wegenenverkeer.mosaic.infrastructure.atom.Indexing.{IndexRequired, PostgresIndexingActor}
import be.wegenenverkeer.uuid.UUIDGen
import com.softwaremill.tagging.@@
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend

import scala.concurrent.duration._

/**
  * Een FeedService wrapper die de calls naar de echte FeedService wrapped binnen een DBIO en automatisch de juiste context meegeeft.
  */
class Slick3AwareFeedService[T](feedStore: PostgresEventStore[T], defaultMeta: FeedMetadata, indexer: ActorRef @@ PostgresIndexingActor) {

  def push(uuid: UUID, elements: Iterable[T]): DBIO[Unit] = {
    SimpleDBIO[Unit] { context =>
      val writer = feedStore.createWriter(context.connection)

      val updated = OffsetDateTime.now()

      elements.foldLeft(UUIDGen(uuid.toString)) {
        case (uuidGen, element) =>
          val next = uuidGen.next
          writer.push(new Event[T](next.uuid.toString, element, updated))
          next
      }

      indexRequiredIf(elements.nonEmpty)
    }
  }

  def push(uuid: UUID, element: T): DBIO[Unit] = {
    SimpleDBIO[Unit] { context =>
      val writer = feedStore.createWriter(context.connection)

      val updated = OffsetDateTime.now()

      writer.push(new Event[T](uuid.toString, element, updated))

      indexRequiredIf(true)
    }
  }

  def getFeedPage(pageNum: Long, pageSize: Int): DBIO[FeedPage[T]] = {
    SimpleDBIO { context =>
      getFeedPage(pageNum, pageSize, context)
    }
  }

  private def getFeedPage(pageNum: Long, pageSize: Int, context: JdbcBackend#JdbcActionContext) = {
    val reader = feedStore.createReader(context.connection)

    val meta =
      if (defaultMeta.getPageSize == pageSize) {
        defaultMeta
      } else {
        new FeedMetadata(pageSize, defaultMeta.getFeedUrl, defaultMeta.getFeedName)
      }

    val pageProvider = FeedPageProviderAdapters.adapt(reader, meta)

    pageProvider.getFeedPage(new FeedPageRef(pageNum))
  }

  def getHeadOfFeed: DBIO[FeedPage[T]] = {
    SimpleDBIO[FeedPage[T]] { context =>
      val reader = feedStore.createReader(context.connection)

      val pageProvider = FeedPageProviderAdapters.adapt(reader, defaultMeta)

      pageProvider.getFeedPage(pageProvider.getHeadOfFeedRef)
    }
  }

  private def indexRequiredIf(condition: Boolean): Unit = {
    if (condition) {
      // geef slick 1 sec tijd om dbio te committen, daarna doen we de index
      // als dat niet voldoende tijd is, zal het later via een ander event of de ReceiveTimeout gebeuren
      // we kunnen geen UpdateIndex sturen in de andThen of flatMap op de dbio van de insert,
      // omdat de transactie mogelijks nog niet gecommit is en de index operatie te snel zou kunnen starten
      // ideaal zou zijn op de andThen of flatMap op de future van de dbio.run, maar daar kunnen we hier niet aan
      indexer ! IndexRequired(timeout = 1.second)
    }
  }

  /// ----- LEGACY SUPPORT ------
  private lazy val seqNrOpBasisVanStartExclusiveStartPreparedStatement = {
    val meta = feedStore.getJdbcEntryStoreMetadata

    val tableName           = meta.getTableName
    val primaryKeyColumn    = meta.getPrimaryKeyColumnName
    val sequenceNoKeyColumn = meta.getSequenceNoColumnName

    val sql =
      s"""
         | SELECT "$sequenceNoKeyColumn"
         | FROM "$tableName"
         | WHERE "$primaryKeyColumn" > ?
         | ORDER BY "$primaryKeyColumn" ASC
         | LIMIT 1;
      """.stripMargin.trim.filterNot(_ == '\n')
    sql
  }

  /**
    *
    * @param start the starting entry's sequence number (exclusive), should not be returned in the feed page
    * @param pageSize the maximum number of feed entries to return. The page could contain less entries
    * @param forward if true navigate to 'previous' elements in feed (towards head of feed)
    *                else ('backward') navigate to 'next' elements in feed (towards last page of feed) -- NOT SUPPORTED
    * @return
    */
  def getFeedPage(start: Long, pageSize: Int, forward: Boolean): DBIO[FeedPage[T]] = {
    SimpleDBIO { context =>
      val pageNum = getPageNum(start, pageSize, context)
      getFeedPage(pageNum, pageSize, context)
    }
  }

  private def getPageNum(start: Long, pageSize: Int, context: JdbcBackend#JdbcActionContext): Long = {
    if (start == 0) {
      0
    } else {
      val stmt = context.connection.prepareStatement(seqNrOpBasisVanStartExclusiveStartPreparedStatement)
      stmt.setLong(1, start)

      val resultSet = stmt.executeQuery()

      val startSequenceNr =
        if (resultSet.next) {
          resultSet.getLong(1)
        } else {
          throw new IndexOutOfBoundsException("Requested page not found")
        }

      startSequenceNr / pageSize
    }
  }
}

object Indexing {
  class PostgresIndexingActor(
      feedStore: PostgresEventStore[_],
      marker: String,
      maxReindexInterval: Duration
  )(implicit dbRunner: DbRunner)
      extends Actor
      with ActorLogging {

    import context.dispatcher

    override def preStart(): Unit = {
      // doe een indexatie bij opstarten
      self ! UpdateIndex
      context.setReceiveTimeout(maxReindexInterval)
    }

    def receive: Receive = waitingForReindex

    def waitingForReindex: Receive = {
      case UpdateIndex | ReceiveTimeout =>
        context.become(reindexing())
        reindex()

      case IndexRequired(timeout) =>
        context.system.scheduler.scheduleOnce(timeout, self, UpdateIndex)
        ()
    }

    def reindexing(extraReindexRequest: Boolean = false): Receive = {
      case IndexUpdated if extraReindexRequest =>
        context.become(reindexing())
        reindex()

      case IndexUpdated =>
        context.become(waitingForReindex)

      case IndexFailed(f) =>
        context.become(waitingForReindex)

      case UpdateIndex | ReceiveTimeout =>
        // ReceiveTimeout kan alleen maar als index heel lang duurt en er ondertussen geen elementen bijgevoegd zijn
        context.become(reindexing(extraReindexRequest = true))

      case IndexRequired(timeout) =>
        context.system.scheduler.scheduleOnce(timeout, self, UpdateIndex)
        ()
    }

    private def reindex(): Unit = {
      SimpleDBIO[Unit](context => feedStore.index(context.connection))
        .run(s"feedstore.$marker.index")
        .map(_ => IndexUpdated)
        .recover {
          case t => IndexFailed(t)
        }
        .pipeTo(self)
      ()
    }
  }

  sealed trait Protocol

  case object UpdateIndex extends Protocol

  case class IndexRequired(timeout: FiniteDuration) extends Protocol

  private[Indexing] case object IndexUpdated extends Protocol

  private[Indexing] case class IndexFailed(t: Throwable) extends Protocol

  case class Config(marker: String, maxReindexInterval: Duration)

  object PostgresIndexingActor {
    def props(feedStore: PostgresEventStore[_], config: Config)(implicit dbRunner: DbRunner) =
      Props(new PostgresIndexingActor(feedStore, config.marker, config.maxReindexInterval))
  }
}
