package be.wegenenverkeer.mosaic.test.painter
import java.awt.Dimension
import java.awt.image.RenderedImage
import java.io.File

import be.wegenenverkeer.mosaic.application.painters.OpstellingImagePainter
import be.wegenenverkeer.mosaic.domain.model.{CRS, GeoJson}
import javax.imageio.ImageIO
import org.geolatte.geom.{C2D, Envelope}
import org.geolatte.maprenderer.java2D.AWTMapGraphics
import be.wegenenverkeer.mosaic.test.TestSupport.assertImageEquals
import org.scalatest.FunSuite
import play.api.libs.json._

import scala.io.Source

import be.wegenenverkeer.mosaic.domain.model.VerkeersbordenFormatters._

class OpstellingImagePainterTest extends FunSuite {

  // https://apps.mow.vlaanderen.be/geolatte-nosqlfs/api/databases/featureserver/verkeersborden/query?bbox=152911.5,209130.8125,153212.5,209330.9375
  test("Visuele verificatie opstelling verschillende resoluties - zie /tmp") {

    val bufferedSource = Source.fromURL(getClass.getResource("/opstellingen.chunked.json"))

    val geoJsons = bufferedSource.getLines
      .map(line => {
        Json.parse(line).validate[GeoJson] match {
          case JsSuccess(opstelling, _) => opstelling
          case e: JsError               => sys.error("Parse error: " + JsError.toJson(e).toString())
        }
      })
      .toList

    bufferedSource.close

    val aantalTiles = 5

    val tileSize = 256 * aantalTiles
    val gridSize = 32

    val originX = 152911.5
    val originY = 209130.8125

    val dim = new Dimension(tileSize, tileSize)

    def tekenOpstellingen(gridSize: Double): Unit = {

      val extent = new Envelope[C2D](
        new C2D(originX, originY),
        new C2D(originX + gridSize * aantalTiles, originY + gridSize * aantalTiles),
        CRS.LAMBERT72
      )

      val mapGraphics = new AWTMapGraphics(dim, extent)

      val painter = new OpstellingImagePainter(mapGraphics)
      geoJsons.foreach(geoJson => painter.paint(geoJson.asPlanarFeature()))
      val img = mapGraphics.renderImage
      ImageIO.write(img, "PNG", new File("/tmp", s"verkeersborden - resolutie ${gridSize * aantalTiles / tileSize}.png"))
    }

    Seq(32, 64, 128, 256.0).foreach(tekenOpstellingen)
  }

  // https://wms2.apps.mow.vlaanderen.be/geowebcache/service/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fpng&
  // TRANSPARENT=true&LAYERS=dienstkaart-grijs&TILED=true&SRS=EPSG%3A31370&WIDTH=256&HEIGHT=256&STYLES=&BBOX=152976%2C209159.75%2C153008%2C209191.75

  test("Tiles alles resoluties") {

    val bufferedSource = Source.fromURL(getClass.getResource("/opstellingen.chunked.json"))

    val geoJsons = bufferedSource.getLines
      .map(line => {
        Json.parse(line).validate[GeoJson] match {
          case JsSuccess(opstelling, _) => opstelling
          case e: JsError               => sys.error("Parse error: " + JsError.toJson(e).toString())
        }
      })
      .toList

    bufferedSource.close

    val aantalMetaTiles   = 4
    val origin            = Seq(152912, 209159.75)
    val tileSize          = 256
    val teTestenGridSizes = Seq(32.0, 64.0, 128.0, 256.0)

    for (gridSize <- teTestenGridSizes) {
      for (x <- origin.head to (origin.head + gridSize * aantalMetaTiles) by gridSize) {
        for (y <- origin.last to (origin.last + gridSize * aantalMetaTiles) by gridSize) {
          val resolution = gridSize / tileSize

          val (minX, minY, maxX, maxY) = (x, y, x + gridSize, y + gridSize)

          val img = generateTile(minX, minY, maxX, maxY, geoJsons, tileSize)

          // ImageIO.write(img, "PNG", new File("/tmp", s"tile-$minX-$minY-$maxX-$maxY.png"))

          // we first write the rendered file to disk, because writing to disk and then reading might cause slight
          // changes - presumably due to rounding to int pixels.
          val tmpFile = File.createTempFile("tmp", "png")
          ImageIO.write(img, "PNG", tmpFile)
          val received = ImageIO.read(tmpFile)

          assertImageEquals(s"resolutie/$resolution/tile-$minX-$minY-$maxX-$maxY-expected.png", received)
        }
      }
    }
  }

  //  https://wms3.apps.mow.vlaanderen.be/geowebcache/service/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&LAYERS=dienstkaart-grijs&
  //  TILED=true&SRS=EPSG%3A31370&WIDTH=256&HEIGHT=256&STYLES=&BBOX=152944%2C209159.75%2C152976%2C209191.75
  private def generateTile(minX: Double,
                           minY: Double,
                           maxX: Double,
                           maxY: Double,
                           geoJsons: List[GeoJson],
                           tileSize: Int): RenderedImage = {

    val extent      = new Envelope[C2D](new C2D(minX, maxY), new C2D(maxX, minY), CRS.LAMBERT72)
    val dim         = new Dimension(tileSize, tileSize)
    val mapGraphics = new AWTMapGraphics(dim, extent)
    val painter     = new OpstellingImagePainter(mapGraphics)

    geoJsons.foreach(geoJson => painter.paint(geoJson.asPlanarFeature()))

    mapGraphics.renderImage
  }
}
