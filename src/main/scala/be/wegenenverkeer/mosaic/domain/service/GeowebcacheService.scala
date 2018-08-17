package be.wegenenverkeer.mosaic.domain.service

import java.net.URL

import be.wegenenverkeer.api.geowebcache.GeowebcacheApi
import be.wegenenverkeer.api.geowebcache.Type._
import be.wegenenverkeer.api.geowebcache.dsl.scalaplay.client.ClientConfig
import be.wegenenverkeer.mosaic.util.{Base64Conversion, Logging}
import be.wegenenverkeer.restfailure.{RestException, RestFailure, _}
import org.geolatte.geom.{C2D, Envelope}
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

class GeowebcacheService(configuration: Configuration) extends Logging with Base64Conversion {

  private val baseUrl =
    configuration.getOptional[String]("geowebcache.url").getOrElse(sys.error("Heb een waarde nodig voor geowebcache.url"))
  private val user =
    configuration.getOptional[String]("geowebcache.user").getOrElse(sys.error("Heb een waarde nodig voor geowebcache.user"))
  private val password =
    configuration.getOptional[String]("geowebcache.password").getOrElse(sys.error("Heb een waarde nodig voor geowebcache.password"))

  val geowebcacheApi = GeowebcacheApi(
    url    = new URL(baseUrl),
    config = ClientConfig(requestTimeout = 10000),
    defaultHeaders = Map(("Authorization", "Basic " + encodeBasicAuth(user, password)))
  )

  def invalidate(envelope: Envelope[C2D])(implicit exc: ExecutionContext): Future[Unit] = {

    geowebcacheApi.rest.seed
      .layer("mosaic")
      .post(
        `type`      = truncate,
        format      = "image/png",
        gridSetId   = "EPSG:31370",
        minX        = envelope.lowerLeft.getX,
        minY        = envelope.lowerLeft.getY,
        maxX        = envelope.upperRight.getX,
        maxY        = envelope.upperRight.getY,
        zoomStart   = 0,
        zoomStop    = 15,
        threadCount = 1
      )
      .flatMap { response =>
        if (response.status == 200) {
          Future.successful(Unit)
        } else {
          Future.failed(new RestException(RestFailure.fromStatus(response.status, response.stringBody.getOrElse("").asErrorMessage)))
        }
      }
  }

}
