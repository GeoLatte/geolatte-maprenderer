package be.wegenenverkeer.mosaic.domain.model

import java.util

import org.geolatte.geom.crs.{CrsRegistry, ProjectedCoordinateReferenceSystem}
import org.geolatte.geom.{C2D, Feature, Geometry, Point => GeolattePoint}

import collection.JavaConverters._
import play.api.libs.json.{Format, Json}
import org.geolatte.geom

case class Opstelling(id: Long, aanzichten: Seq[Aanzicht], delta: Option[Float], binaireData: BinaireOpstellingData, locatie: Point)
case class Aanzicht(id: Long, anker: Point, hoek: Float, binaireData: BinaireAanzichtData)
case class BinaireOpstellingData(kaartvoorstelling: ImageData)
case class BinaireAanzichtData(platgeslagenvoorstelling: ImageData, platgeslagenvoorstellingklein: ImageData)
case class ImageData(properties: ImageDimensie, mime: String, data: String)
case class ImageDimensie(breedte: String, hoogte: String)
case class Point(coordinates: Seq[Double]) {
 def asGeolattePoint(): GeolattePoint[C2D] = {
   new GeolattePoint(new C2D(coordinates.head, coordinates.last), CRS.LAMBERT72)
  }
}

object VerkeersbordenFormatters {
  implicit lazy val opstellingFormat: Format[Opstelling]                       = Json.format[Opstelling]
  implicit lazy val aanzichtFormat: Format[Aanzicht]                           = Json.format[Aanzicht]
  implicit lazy val binaireOpstellingDataFormat: Format[BinaireOpstellingData] = Json.format[BinaireOpstellingData]
  implicit lazy val binaireAanzichtDataFormat: Format[BinaireAanzichtData]     = Json.format[BinaireAanzichtData]
  implicit lazy val imageDataFormat: Format[ImageData]                         = Json.format[ImageData]
  implicit lazy val imageDimensieFormat: Format[ImageDimensie]                 = Json.format[ImageDimensie]
  implicit lazy val pointFormat: Format[Point]                                 = Json.format[Point]
}

object CRS {
  val LAMBERT72: ProjectedCoordinateReferenceSystem = CrsRegistry.getProjectedCoordinateReferenceSystemForEPSG(31370)
}

class OpstellingFeature(opstelling: Opstelling) extends Feature[C2D, String] {

  def getOpstelling = opstelling

  override def getGeometry: Geometry[C2D] = {
    new geom.Point[C2D](new C2D(opstelling.locatie.coordinates.head, opstelling.locatie.coordinates.last), CRS.LAMBERT72)
  }
  override def getId: String                           = opstelling.id.toString
  override def getProperties: util.Map[String, AnyRef] = Map("properties" -> this.asInstanceOf[AnyRef]).asJava
}

class AanzichtFeature(opstelling: Opstelling, aanzicht: Aanzicht) extends Feature[C2D, String] {

  override def getGeometry: Geometry[C2D] = {
    new geom.Point[C2D](new C2D(opstelling.locatie.coordinates.head, opstelling.locatie.coordinates.last), CRS.LAMBERT72)
  }
  override def getId: String                           = aanzicht.id.toString
  override def getProperties: util.Map[String, AnyRef] = Map("properties" -> this.asInstanceOf[AnyRef]).asJava
}
