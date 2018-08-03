package be.wegenenverkeer.mosaic.api

import java.net.URI
import java.util
import java.util.Optional

import akka.util.ByteString
import org.geolatte.mapserver.http.{ HttpHeaders, HttpQueryParams, HttpRequest }
import org.geolatte.mapserver.ows.OwsHttpService
import play.api.mvc._

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters
import scala.concurrent.ExecutionContext

class WmsController(val controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext) extends BaseController {

  def wms(): Action[AnyContent] = Action.async { implicit request =>
    val owsHttpService = new OwsHttpService()

    val javafuture = owsHttpService.process(PlayRequestToGeolatteHttpRequestWrapper(request))

    FutureConverters.toScala(javafuture).map { result =>
      val headers: Map[String, String] =
        result.headers().map().asScala.toMap.flatMap {
          case (key, values) => values.asScala.map(value => (key, value))
        }

      val (contentTypeHeaders, otherHeaders) = headers.partition(_._1.equalsIgnoreCase("Content-Type"))

      val contentType = contentTypeHeaders.values.headOption

      val responseHeader = ResponseHeader(
        status  = result.statusCode(),
        headers = otherHeaders
      )

      Result(header = responseHeader, body = play.api.http.HttpEntity.Strict(ByteString(result.body()), contentType))
    }

  }

}

case class PlayRequestToGeolatteHttpRequestWrapper(request: Request[_]) extends HttpRequest {
  override def uri(): URI                    = new URI(request.uri)
  override def method(): String              = request.method
  override def headers(): HttpHeaders        = PlayRequestHeadersToGeolatteHttpHeadersWrapper(request.headers)
  override def parseQuery(): HttpQueryParams = PlayRequestQueryStringToGeolatteHttpQueryParamsWrapper(request.queryString)
}

case class PlayRequestHeadersToGeolatteHttpHeadersWrapper(headers: Headers) extends HttpHeaders {
  override def allValues(name: String): util.List[String] = headers.asJava.getAll(name)
  override def firstValue(name: String): Optional[String] = headers.asJava.get(name)
  override def map(): util.Map[String, util.List[String]] = headers.asJava.toMap
}

case class PlayRequestQueryStringToGeolatteHttpQueryParamsWrapper(queryString: Map[String, Seq[String]]) extends HttpQueryParams {
  override def allValues(name: String): util.List[String] = queryString.get(name).toList.flatten.asJava
  override def firstValue(name: String): Optional[String] = Optional.ofNullable(queryString.get(name).flatMap(_.headOption).orNull)
  override def allParams(): util.Set[String]              = queryString.keySet.asJava
}
