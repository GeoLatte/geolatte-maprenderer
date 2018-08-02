package be.wegenenverkeer.mosaic.infrastructure

import java.util.UUID

import akka.persistence.pg.journal.PgAsyncWriteJournal
import io.funcqrs.EventId
import be.wegenenverkeer.mosaic.domain.model.CommonFormats._
import play.api.libs.json.{JsError, JsObject, JsSuccess}

class CustomPgAsyncWriteJournal extends PgAsyncWriteJournal {

  /**
    * Indien mogelijk, gebruiken we de eventId uit onze events in plaats van een gegenereerde UUID.
    *
    * @param event event (JsValue)
    * @return de eventId uit het event of random UUID
    */
  override def getUuid(event: Any): String = {
    event match {
      case jsObject: JsObject => extractEventId(jsObject).getOrElse(UUID.randomUUID.toString)
      case _                  => UUID.randomUUID.toString
    }
  }

  /**
    * Een aggregate actor heeft een protocol event als event (met in de metadata een EventId)
    */
  def extractEventId(value: JsObject): Option[String] = {
    val eventIdResult = (value \ "metadata" \ "eventId").validate[EventId]
    eventIdResult match {
      case JsSuccess(eventId, _) => Some(eventId.value.toString)
      case JsError(errors)       => None
    }
  }

}