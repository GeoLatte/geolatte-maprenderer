package be.wegenenverkeer.mosaic.domain.model.verkeersbord

import java.time.OffsetDateTime
import java.util.UUID

import be.wegenenverkeer.json.TypedJson.{TypeHintFormat, _}
import play.api.libs.json.{JsResult, JsValue, Json}

/** De verkeersborden feed events zoals ze binnenkomen via atomium feed */
object VerkeersbordenChangeFeedEvents {

  sealed trait VerkeersbordenChangeFeedEvent {

    def entityId: Long

    def uuid: Option[UUID]

    def entityRef: String

    def ident8: Option[String]

    def occurred: OffsetDateTime

    def recorded: OffsetDateTime

  }

  case class UpsertFeedEvent(
      entityId: Long,
      uuid: Option[UUID],
      entityRef: String,
      ident8: Option[String],
      occurred: OffsetDateTime,
      recorded: OffsetDateTime
  ) extends VerkeersbordenChangeFeedEvent

  case class DeleteFeedEvent(
      entityId: Long,
      uuid: Option[UUID],
      entityRef: String,
      ident8: Option[String],
      occurred: OffsetDateTime,
      recorded: OffsetDateTime
  ) extends VerkeersbordenChangeFeedEvent

  object VerkeersbordenChangeFeedEvent {

    implicit val format = new TypeHintFormat[VerkeersbordenChangeFeedEvent](
      typeHintKey = "change",
      Json.format[UpsertFeedEvent].withTypeHint("UPSERT"),
      Json.format[DeleteFeedEvent].withTypeHint("DELETE")
    )
  }

  /**
    * Van JsonNode naar VerkeersbordenFeedEvent
    * (van atomium krijgen we JsonNode en geen String of play-json)
    */
  def fromJsValue(jsValue: JsValue): JsResult[VerkeersbordenChangeFeedEvent] = {
    Json.fromJson[VerkeersbordenChangeFeedEvent](jsValue)(VerkeersbordenChangeFeedEvent.format)
  }
}
