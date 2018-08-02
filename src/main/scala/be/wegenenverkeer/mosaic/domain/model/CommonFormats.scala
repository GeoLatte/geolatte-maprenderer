package be.wegenenverkeer.mosaic.domain.model

import java.time.OffsetDateTime
import java.util.UUID

import io.funcqrs.{ CommandId, EventId, Tag }
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.{ Failure, Success, Try }

trait CommonFormats {

  /**
    * Json format for case classes that just wrap a String value. The json representation is just the string.
    */
  def simpleStringWrapper[W](creator: String => W, extractor: W => Option[String]): Format[W] = {

    new Format[W] {
      override def reads(json: JsValue): JsResult[W] = {
        json.validate[String].map(creator)
      }

      override def writes(o: W): JsValue = {
        JsString(extractor(o).get)
      }
    }
  }

  def simpleUUIDWrapper[W](creator: UUID => W, extractor: W => Option[UUID]): Format[W] = {

    new Format[W] {
      override def reads(json: JsValue): JsResult[W] = {
        json.validate[String].map { string =>
          creator(UUID.fromString(string))
        }
      }

      override def writes(o: W): JsValue = {
        JsString(extractor(o).get.toString)
      }
    }
  }

  implicit val offsetDateTimeFormat = new Format[OffsetDateTime] {
    override def reads(json: JsValue) = json match {
      case JsString(s) =>
        Try(OffsetDateTime.parse(s)) match {
          case Success(dateTime) => JsSuccess(dateTime)
          case Failure(_)        => JsError("error.expected.datetime")
        }
      case _ => JsError("error.expected.jsstring")
    }

    override def writes(o: OffsetDateTime): JsValue = JsString(o.toString)
  }

  private def readUUID(value: String): JsResult[UUID] = {
    Try(UUID.fromString(value))
      .map(JsSuccess(_))
      .getOrElse(JsError("error.expected.uuid"))
  }

  def readJsonId[T](json: JsValue)(toJsResult: (String) => JsResult[T]) = {
    json match {
      case JsString(id) => toJsResult(id)
      case _            => JsError("error.expected.jsstring")
    }
  }

  implicit val commandIdFormat = new Format[CommandId] {
    override def reads(json: JsValue): JsResult[CommandId] =
      readJsonId(json)(id => readUUID(id).map(CommandId))

    override def writes(o: CommandId): JsValue = JsString(o.value.toString)
  }

  implicit val eventIdFormat = new Format[EventId] {
    override def reads(json: JsValue): JsResult[EventId] =
      readJsonId(json)(id => readUUID(id).map(EventId))

    override def writes(o: EventId): JsValue = JsString(o.value.toString)
  }

  implicit val tagFormat = Json.format[Tag]

}

object CommonFormats extends CommonFormats
