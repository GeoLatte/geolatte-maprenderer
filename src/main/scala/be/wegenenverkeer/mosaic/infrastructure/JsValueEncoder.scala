package be.wegenenverkeer.mosaic.infrastructure

import akka.persistence.pg.JsonString
import akka.persistence.pg.event.JsonEncoder
import be.wegenenverkeer.json.TypedJson._
import io.funcqrs.akka.PersistedOffsetAkka.LastProcessedEventOffset
import play.api.Logger
import play.api.libs.json._

class JsValueEncoder extends JsonEncoder {

  val logger = Logger(classOf[JsValueEncoder])

  val formatLastProcessedEventOffset =
    TypeHintFormat(Json.format[LastProcessedEventOffset].withTypeHint)

  implicit val composedFormat: Format[AnyRef] = formatLastProcessedEventOffset ++ formatLastProcessedEventOffset

  def toJson = {

    //case evt: SysteemGebruikerEvent    => JsonString(Json.stringify(Json.toJson(evt)))
    //case evt: LastProcessedEventOffset => JsonString(Json.stringify(Json.toJson(evt)))

    // catch all! We persisteren NOOIT events in binary format
    // !!! akka-persistence-pg slaan de event als binary op als geen match er is
    case onbekendEvent =>
      logger.error(
        s"""
           |==================================================================
           |Onbekend event: $onbekendEvent!!!
           |Nakijken of alle events zijn gedeclareerd in JsValueEncoder
           |==================================================================
         """.stripMargin
      )
      sys.error(s"Onbekend event $onbekendEvent!!!")

  }

  def fromJson = {
    case (jsonString: JsonString, _) =>
      composedFormat.reads(Json.parse(jsonString.value)) match {
        case JsSuccess(v, _) => v
        case JsError(e)      => sys.error(e.toString())
      }
  }
}
