package be.wegenenverkeer.mosaic.domain.service.atomium

import akka.util.Timeout
import be.wegenenverkeer.api.verkeersborden.model.OpstellingPropertiesStatus._
import be.wegenenverkeer.atomium.extension.feedconsumer.FeedEntryConsumer
import be.wegenenverkeer.mosaic.domain.model.verkeersbord.VerkeersbordenChangeFeedEvents
import be.wegenenverkeer.mosaic.domain.model.verkeersbord.VerkeersbordenChangeFeedEvents._
import be.wegenenverkeer.mosaic.domain.service.VerkeersbordenService
import be.wegenenverkeer.mosaic.util.Logging
import io.funcqrs.akka.backend.AkkaBackend
import play.api.libs.json.JsValue
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._

class VerkeersbordenChangeEventConsumer(
    verkeersbordenService: VerkeersbordenService,
    akkaBackend: AkkaBackend
)(implicit ec: ExecutionContext)
    extends FeedEntryConsumer
    with Logging {

  implicit val timeout = Timeout(10.seconds)

  override def consume(changeEvent: JsValue) = {
    DBIO.from(consumeChangeEvent(VerkeersbordenChangeFeedEvents.fromJsValue(changeEvent).get))
  }

  // Consumes the change event in a blocking manner
  private def consumeChangeEvent(verkeersbordenChangeFeedEvent: VerkeersbordenChangeFeedEvent): Future[Unit] = {

    verkeersbordenChangeFeedEvent match {
      case event: UpsertFeedEvent => behandelUpsertEvent(event)
      case event: DeleteFeedEvent => behandelDeleteEvent(event)

      case otherEvent =>
        logger.error(s"Unrecognised event: $otherEvent")
        Future.successful(())
    }

  }

  private def behandelUpsertEvent(evt: UpsertFeedEvent): Future[Unit] = {
    logger.debug(s"behandelUpsertEvent in ChangeEventConsumer $evt")

    verkeersbordenService.getOpstelling(evt.entityId).map { opstelling =>
      opstelling
        .filter { op =>
          op.properties.status == ACTUEEL || op.properties.status == VIRTUEEL
        }
    }
  }

  private def behandelDeleteEvent(evt: DeleteFeedEvent): Future[Unit] = {
    logger.info(s"behandelDeleteEvent in ChangeEventConsumer  $evt")
    Future.successful(Unit)
  }

}
