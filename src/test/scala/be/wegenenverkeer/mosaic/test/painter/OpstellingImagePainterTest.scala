package be.wegenenverkeer.mosaic.test.painter
import java.awt.Dimension
import java.awt.image.RenderedImage
import java.io.File

import be.wegenenverkeer.mosaic.application.painters.OpstellingImagePainter
import be.wegenenverkeer.mosaic.domain.model.{ CRS, Opstelling, Point }
import org.geolatte.geom.Envelope
import org.scalatest.FunSuite
import play.api.libs.json._
import java.util

import javax.imageio.ImageIO
import org.geolatte.geom.{ C2D, Feature, Geometry, Point => GeolattePoint }
import org.geolatte.maprenderer.java2D.AWTMapGraphics
import org.geolatte.maprenderer.map.PlanarFeature
import org.geolatte.test.TestSupport.assertImageEquals

import collection.JavaConverters._
import scala.io.{ BufferedSource, Source }

class OpstellingImagePainterTest extends FunSuite {

  case class GeoJson(id: String, geometry: Point, properties: Opstelling) {
    def asPlanarFeature(): PlanarFeature = PlanarFeature.from(new GeojsonFeature(this))
  }

  import be.wegenenverkeer.mosaic.domain.model.VerkeersbordenFormatters._
  implicit val geojsonFormatter: Format[GeoJson] = Json.format[GeoJson]

  class GeojsonFeature(geoJson: GeoJson) extends Feature[C2D, String] {

    override def getGeometry: Geometry[C2D] = {
      new GeolattePoint[C2D](new C2D(geoJson.geometry.coordinates.head, geoJson.geometry.coordinates.last), CRS.LAMBERT72)
    }
    override def getId: String                           = geoJson.id
    override def getProperties: util.Map[String, AnyRef] = Map("properties" -> geoJson.properties.asInstanceOf[AnyRef]).asJava
  }

  test("Rendering opstellingen voorbeeld 1") {

    val bufferedSource = Source.fromURL(getClass.getResource("/opstellingen.chunked.1.json"))

    val geoJsons = bufferedSource.getLines.map(line => {
      Json.parse(line).validate[GeoJson] match {
        case JsSuccess(opstelling, _) => opstelling
        case e: JsError               => sys.error("Parse error: " + JsError.toJson(e).toString())
      }
    })

    val extent      = new Envelope[C2D](new C2D(153018, 208719), new C2D(153443, 209124), CRS.LAMBERT72)
    val dim         = new Dimension(1800, 1600)
    val mapGraphics = new AWTMapGraphics(dim, extent)
    val painter     = new OpstellingImagePainter(mapGraphics)

    geoJsons.foreach(geoJson => painter.paint(geoJson.asPlanarFeature()))

    bufferedSource.close

    ImageIO.write(mapGraphics.renderImage, "PNG", new File("/tmp", "verkeersborden 1.png"))
  }

  // https://apps.mow.vlaanderen.be/geolatte-nosqlfs/api/databases/featureserver/verkeersborden/query?bbox=152911.5,209130.8125,153212.5,209330.9375
  test("Rendering opstellingen voorbeeld 2") {

    val bufferedSource = Source.fromURL(getClass.getResource("/opstellingen.chunked.2.json"))

    val geoJsons = bufferedSource.getLines.map(line => {
      Json.parse(line).validate[GeoJson] match {
        case JsSuccess(opstelling, _) => opstelling
        case e: JsError               => sys.error("Parse error: " + JsError.toJson(e).toString())
      }
    })

    val extent      = new Envelope[C2D](new C2D(152911.5, 209130.8125), new C2D(153212.5, 209330.9375), CRS.LAMBERT72)
    val dim         = new Dimension(1800, 1600)
    val mapGraphics = new AWTMapGraphics(dim, extent)
    val painter     = new OpstellingImagePainter(mapGraphics)

    geoJsons.foreach(geoJson => painter.paint(geoJson.asPlanarFeature()))

    bufferedSource.close

    ImageIO.write(mapGraphics.renderImage, "PNG", new File("/tmp", "verkeersborden 2.png"))
  }

  // https://wms2.apps.mow.vlaanderen.be/geowebcache/service/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fpng&
  // TRANSPARENT=true&LAYERS=dienstkaart-grijs&TILED=true&SRS=EPSG%3A31370&WIDTH=256&HEIGHT=256&STYLES=&BBOX=152976%2C209159.75%2C153008%2C209191.75

  test("Tiles") {

    val bufferedSource = Source.fromURL(getClass.getResource("/opstellingen.chunked.2.json"))

    val geoJsons = bufferedSource.getLines
      .map(line => {
        Json.parse(line).validate[GeoJson] match {
          case JsSuccess(opstelling, _) => opstelling
          case e: JsError               => sys.error("Parse error: " + JsError.toJson(e).toString())
        }
      })
      .toList

    val aantalTiles = 4

    val origin = Seq(152912, 209159.75)

    val gridSize = 32

    val tileSize = 256

    for (x <- origin.head to (origin.head + gridSize * aantalTiles) by gridSize) {
      for (y <- origin.last to (origin.last + gridSize * aantalTiles) by gridSize) {
        val (minX, minY, maxX, maxY) = (x, y, x + gridSize, y + gridSize)

        val img = generateTile(minX, minY, maxX, maxY, bufferedSource, geoJsons)

        // TODO -- why is this necessary here?
        // we first write the rendered file to disk, because writing to disk and then reading might cause slight
        // changes - presumably due to rounding to int pixels.
        val tmpFile = File.createTempFile("tmp", "png")
        ImageIO.write(img, "PNG", tmpFile)
        val received = ImageIO.read(tmpFile)
        assertImageEquals(s"tile-$minX-$minY-$maxX-$maxY-expected.png", received)
      }
    }
  }

//  https://wms3.apps.mow.vlaanderen.be/geowebcache/service/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&LAYERS=dienstkaart-grijs&
  // TILED=true&SRS=EPSG%3A31370&WIDTH=256&HEIGHT=256&STYLES=&BBOX=152944%2C209159.75%2C152976%2C209191.75
  private def generateTile(minX: Double,
                           minY: Double,
                           maxX: Double,
                           maxY: Double,
                           bufferedSource: BufferedSource,
                           geoJsons: List[GeoJson]): RenderedImage = {

    val extent      = new Envelope[C2D](new C2D(minX, maxY), new C2D(maxX, minY), CRS.LAMBERT72)
    val dim         = new Dimension(256, 256)
    val mapGraphics = new AWTMapGraphics(dim, extent)
    val painter     = new OpstellingImagePainter(mapGraphics)

    geoJsons.foreach(geoJson => painter.paint(geoJson.asPlanarFeature()))

    bufferedSource.close

    mapGraphics.renderImage
  }
}
