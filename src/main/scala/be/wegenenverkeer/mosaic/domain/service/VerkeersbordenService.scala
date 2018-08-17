package be.wegenenverkeer.mosaic.domain.service

import java.net.URL

import be.wegenenverkeer.api.verkeersborden.dsl.scalaplay.Response
import be.wegenenverkeer.api.verkeersborden.dsl.scalaplay.client.ClientConfig
import be.wegenenverkeer.api.verkeersborden.model.Opstelling
import be.wegenenverkeer.mosaic.util.Logging
import be.wegenenverkeer.restfailure.{ErrorMessage, RestException, RestFailure, _}
import org.ehcache.{Cache, CacheManager}
import play.api.Configuration
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class VerkeersbordenService(cacheManager: CacheManager, configuration: Configuration) extends Logging {

  val baseUrl: String =
    configuration.getOptional[String]("verkeersborden.url").getOrElse(sys.error("Heb een waarde nodig voor verkeersborden.url"))

  val vkbApi = be.wegenenverkeer.api.verkeersborden.VerkeersbordenApi(
    url    = new URL(baseUrl),
    config = ClientConfig(requestTimeout = 10000)
  )

  val vkbFeedCache: Cache[String, String] = cacheManager.getCache("verkeersbordenFeedPage", classOf[String], classOf[String])

  def getFeedPageCached(page: String)(implicit context: ExecutionContext): Future[String] = {
    Option(vkbFeedCache.get(page)) match {
      case Some(body) =>
        Future.successful(body)
      case None =>
        vkbApi.rest.events.zi.verkeersborden.feed.page(page).get().flatMap { response =>
          response.stringBody match {
            case Some(body) =>
              vkbFeedCache.put(page, body)
              Future.successful(body)
            case None =>
              Future.failed(new Exception("Lege feed page"))
          }
        }
    }
  }

  def getOpstelling(id: Long)(implicit context: ExecutionContext): Future[Option[Opstelling]] = {
    vkbApi.rest.zi.verkeersborden.id(id).get(binaryData = Some(false)).map {
      case response if response.status == 200 => Some(response.body.get)
      case response if response.status == 404 => None
      case response                           => throw new RestException(RestFailure.fromStatus(response.status, extractErrorMessage(response)))
    }
  }

  /**
    * Dit method probeer een error message te extracten van een response body.
    * Het is verwacht dat de body een veld 'message' bevat,
    * maar de body kan zowel een JsValue als een json String zijn
    */
  private def extractErrorMessage(response: Response[_]): ErrorMessage = {

    response.jsonBody
      .orElse {
        // als geen JsonBody,
        // probeer toch de StringBody te parsen en een JsValue te builden
        response.stringBody.flatMap { stringPayload =>
          Try(Json.parse(stringPayload)).toOption
        }
      }
      .flatMap { jsValue =>
        // heeft de JsValue een 'message' veld?
        (jsValue \ "message").validate[String].asOpt.orElse((jsValue \ "error").validate[String].asOpt)
      }
      .orElse {
        // is alles mislukt? dan gebruiken we de inhoud van de StringBody
        // als niet leeg uiteraard
        response.stringBody.map { payload =>
          if (payload.isEmpty) // alles is mislukt en string is leeg
            s"Ongekende fout, status was ${response.status}"
          else payload
        }
      }
      .getOrElse(s"Ongekende fout, status was ${response.status}")
      .asErrorMessage
  }

}
