package be.wegenenverkeer.mosaic.domain.service.atomium

import java.io.{File, FileWriter}

import be.wegenenverkeer.atomium.extension.feedconsumer.FeedEntryConsumer
import be.wegenenverkeer.mosaic.domain.model.verkeersbord.VerkeersbordenChangeFeedEvents
import be.wegenenverkeer.mosaic.domain.model.verkeersbord.VerkeersbordenChangeFeedEvents._
import be.wegenenverkeer.mosaic.util.Logging
import play.api.libs.json.JsValue
import slick.dbio.{DBIO, DBIOAction, Effect, NoStream}

import scala.concurrent.{ExecutionContext, Future}

class VerkeersbordenChangeEventConsumer()(implicit ec: ExecutionContext) extends FeedEntryConsumer with Logging {

  override def consume(changeEvent: JsValue): DBIOAction[Unit, NoStream, Effect] = {
    DBIO.from(consumeChangeEvent(VerkeersbordenChangeFeedEvents.fromJsValue(changeEvent).get))
  }

  private def consumeChangeEvent(evt: VerkeersbordenChangeFeedEvent): Future[Unit] = {
    logger.debug(s"event in ChangeEventConsumer $evt")

    Future {
      writeToFile(evt.envelope)
      writeToFile(evt.vorigeEnvelope)
    }
  }

  def writeToFile(envelope: Option[List[Double]]): Unit = {
    envelope.foreach { env =>
      val fileWriter = new FileWriter(File.createTempFile("tmp", ".json"))
      fileWriter.write(env.mkString("[", ",", "]"))
      fileWriter.close()
    }
  }

}
