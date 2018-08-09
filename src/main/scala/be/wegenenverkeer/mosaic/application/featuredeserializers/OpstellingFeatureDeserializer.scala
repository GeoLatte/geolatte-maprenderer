package be.wegenenverkeer.mosaic.application.featuredeserializers

import be.wegenenverkeer.mosaic.domain.model.GeoJson
import org.geolatte.maprenderer.map.PlanarFeature
import org.geolatte.mapserver.features.FeatureDeserializer
import org.slf4j.LoggerFactory
import play.api.libs.json.{ JsError, JsSuccess, Json }

import collection.JavaConverters._

class OpstellingFeatureDeserializer extends FeatureDeserializer {

  private val logger = LoggerFactory.getLogger(classOf[OpstellingFeatureDeserializer])

  import be.wegenenverkeer.mosaic.domain.model.VerkeersbordenFormatters._

  override def deserialize(jsonString: String): java.lang.Iterable[PlanarFeature] = {
    maybeFeature(jsonString).toList.asJava
  }

  def maybeFeature(json: String): Option[PlanarFeature] = {
    Json.parse(json).validate[GeoJson] match {
      case JsSuccess(geojson, _) =>
        Some(geojson.asPlanarFeature())
      case e: JsError =>
        logger.error("Parse error: " + JsError.toJson(e).toString())
        None
    }
  }

}
