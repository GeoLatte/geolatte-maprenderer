package be.wegenenverkeer.mosaic.domain.service.atomium

import be.wegenenverkeer.atomium.extension.feedconsumer.FeedEntryConsumer
import be.wegenenverkeer.mosaic.domain.model.CRS
import be.wegenenverkeer.mosaic.domain.model.verkeersbord.VerkeersbordenChangeFeedEvents
import be.wegenenverkeer.mosaic.domain.model.verkeersbord.VerkeersbordenChangeFeedEvents._
import be.wegenenverkeer.mosaic.domain.service.storage.EnvelopeStorage
import be.wegenenverkeer.mosaic.util.Logging
import org.geolatte.geom.{C2D, Envelope}
import play.api.libs.json.JsValue
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

class VerkeersbordenChangeEventConsumer(envelopeStorage: EnvelopeStorage)(implicit ec: ExecutionContext)
    extends FeedEntryConsumer
    with Logging {

  override def consume(changeEvent: JsValue): DBIO[Unit] = {
    DBIO.from(consumeChangeEvent(VerkeersbordenChangeFeedEvents.fromJsValue(changeEvent).get))
  }

  private def consumeChangeEvent(evt: VerkeersbordenChangeFeedEvent): Future[Unit] = {
    logger.debug(s"event in ChangeEventConsumer $evt")

    val a = write(evt.envelope)
    val b = write(evt.vorigeEnvelope)

    for {
      _ <- a
      _ <- b
    } yield ()
  }

  def write(envelope: Option[List[Double]]): Future[Unit] = {
    envelope match {
      case Some(minX :: minY :: maxX :: maxY :: Nil) =>
        envelopeStorage.schrijf(new Envelope[C2D](minX, minY, maxX, maxY, CRS.LAMBERT72))

      case Some(list) =>
        logger.warn(s"Kon geen envelope maken van $list")
        Future.successful(Unit)

      case None =>
        Future.successful(Unit)
    }
  }

}
