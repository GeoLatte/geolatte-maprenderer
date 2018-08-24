package be.wegenenverkeer.mosaic.domain.service.geowebcache

import java.net.URL

import be.wegenenverkeer.api.geowebcache.GeowebcacheApi
import be.wegenenverkeer.api.geowebcache.Type._
import be.wegenenverkeer.api.geowebcache.dsl.scalaplay.client.ClientConfig
import be.wegenenverkeer.mosaic.domain.service.painters.OpstellingImagePainter.maxBordImageGrootteInMeter
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
  private val layer =
    configuration.getOptional[String]("geowebcache.layer").getOrElse(sys.error("Heb een waarde nodig voor geowebcache.layer"))

  val geowebcacheApi = GeowebcacheApi(
    url            = new URL(baseUrl),
    config         = ClientConfig(requestTimeout = 10000),
    defaultHeaders = Map(("Authorization", "Basic " + encodeBasicAuth(user, password)))
  )

  // rekening houden met gedraaide borden: die vallen buiten de buffer
  // we berekenen de diagonaal vanuit het punt onderaan in het midden (=draaias) naar een hoek bovenaan
  val bufferVoorBordGrootteInMeter: Double = {
    val maxAfstandAnkerpuntX = maxBordImageGrootteInMeter / 2
    val maxAfstandAnkerpuntY = maxBordImageGrootteInMeter
    val diagonaal = Math.sqrt(Math.pow(maxAfstandAnkerpuntX, 2) + Math.pow(maxAfstandAnkerpuntY, 2))
    val extraMarge = 5
    diagonaal + extraMarge
  }

  def invalidate(envelope: Envelope[C2D])(implicit exc: ExecutionContext): Future[Unit] = {

    geowebcacheApi.rest.seed
      .layer(layer)
      .post(
        `type`      = truncate,
        format      = "image/png",
        gridSetId   = "AWV-gridset-Lambert72",
        minX        = envelope.lowerLeft.getX - bufferVoorBordGrootteInMeter,
        minY        = envelope.lowerLeft.getY - bufferVoorBordGrootteInMeter,
        maxX        = envelope.upperRight.getX + bufferVoorBordGrootteInMeter,
        maxY        = envelope.upperRight.getY + bufferVoorBordGrootteInMeter,
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
