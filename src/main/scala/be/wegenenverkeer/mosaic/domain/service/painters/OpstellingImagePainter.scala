package be.wegenenverkeer.mosaic.domain.service.painters

import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Graphics2D}

import be.wegenenverkeer.mosaic.domain.model._
import be.wegenenverkeer.mosaic.util.Base64Conversion
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.math.Vector2D
import org.geolatte.geom.{C2D, JTSGeometryOperations, Point}
import org.geolatte.maprenderer.map.{MapGraphics, Painter, PlanarFeature}
import org.geolatte.maprenderer.painters.EmbeddedImagePainter
import org.geolatte.maprenderer.painters.EmbeddedImagePainter.ImageExtractor
import org.geolatte.maprenderer.util.ImageUtils
import org.slf4j.LoggerFactory

/**
  *  Tekent een volledige opstelling naar een MapGraphics
  *  Er wordt een PlanarFeature verwacht met onder getProperties.get("properties) een Opstelling
  *
  *  Er worden dezelfde regels gevolgd als bij de frontend rendering van ng-kaart:
  *
  *   resolution = graphics.getMapUnitsPerPixel
  *
  *   if (resolution <= 0.125) {
  *       return geselecteerd ? opstellingMetAanzichten(feature, geselecteerd, false) : opstellingMetAanzichten(feature, geselecteerd, false);
  *     } else if (resolution <= 0.25) {
  *       return geselecteerd ? opstellingMetAanzichten(feature, geselecteerd, true) : opstellingMetAanzichten(feature, geselecteerd, true);
  *     } else if (resolution <= 0.5) {
  *       return opstellingMetHoek(feature, geselecteerd);
  *     } else {
  *       return opstellingAlsPunt(feature, geselecteerd);
  *     }
  *
  *   @param graphics   the graphics to use
  *   @param resolution resolutie die bepaalt welke soort voorstelling getekend wordt.
  *                       Vb: 1 van [1024.0, 512.0, 256.0, 128.0, 64.0, 32.0, 16.0, 8.0, 4.0, 2.0, 1.0, 0.5, 0.25, 0.125, 0.0625, 0.03125]
  */
class OpstellingImagePainter(graphics: MapGraphics) extends Painter with Base64Conversion {

  private val logger = LoggerFactory.getLogger(classOf[OpstellingImagePainter])

  import OpstellingImagePainter._

  def MAX_AFSTAND_ANKERPUNT = 100.0 // maximum afstand tussen opstelling en ankerpunt. Aanzicht wordt dichter gebracht indien groter.

  val REFERENTIE_UPP = 0.125 // de mapUnitsPerPixel die als referentie dient om ankerpunten korter bij te zetten en images te verkleinen

  override def willPaint(): Boolean = {
    graphics.getMapUnitsPerPixel <= 2.0
  }

  override def paint(geojsonPlanarFeature: PlanarFeature): Unit = {

    Option(geojsonPlanarFeature.getProperties.get("properties").asInstanceOf[Opstelling]) match {
      case Some(opstelling) =>
        val opstellingPF = PlanarFeature.from(new OpstellingFeature(opstelling))

        graphics.getMapUnitsPerPixel match {
          case res if res <= 0.125 => renderOpstellingMetAanzichten(opstellingPF, opstelling, res, klein = false)
          case res if res <= 0.25  => renderOpstellingMetAanzichten(opstellingPF, opstelling, res, klein = true)
          case res if res <= 0.5   => renderOpstellingMetHoek(opstellingPF, opstelling)
          case res                 => renderOpstellingAlsPunt(opstellingPF)
        }

      case None =>
        logger.error("Geen opstelling object gevonden in properties van GeoJson")
    }
  }

  private def renderOpstellingAlsPunt(planarFeature: PlanarFeature): Unit = {
    val painter = new PointPainter(graphics, 3)
    painter.paint(planarFeature)
  }

  private def renderOpstellingMetAanzichten(planarFeature: PlanarFeature,
                                            opstelling: Opstelling,
                                            mapUnitsPerPixel: Double,
                                            klein: Boolean): Unit = {
    renderOpstellingMetHoek(planarFeature, opstelling)
    opstelling.aanzichten.foreach(renderAanzicht(_, opstelling, mapUnitsPerPixel, klein))
  }

  private def renderOpstellingMetHoek(planarFeature: PlanarFeature, opstelling: Opstelling): Unit = {
    readImageFromBase64String(opstelling.binaireData.kaartvoorstelling.data).foreach { image =>
      val painter = new ImagePainter(graphics, image)
      painter.paint(planarFeature)
    }
  }

  private def renderAanzicht(aanzicht: Aanzicht, opstelling: Opstelling, mapUnitsPerPixel: Double, klein: Boolean): Unit = {

    def imagesExtractor: ImageExtractor = { feature =>
      val imageData =
        if (klein) {
          aanzicht.binaireData.platgeslagenvoorstellingklein
        } else {
          aanzicht.binaireData.platgeslagenvoorstelling
        }

      ImageUtils
        .readImageFromBase64String(imageData.data)
        .map(image => verkleinTeGroteImage(image, mapUnitsPerPixel))
    }

    val painter = new EmbeddedImagePainter(
      graphics,
      imagesExtractor,
      feature => verzekerMaxAfstandAnkerpunt(opstelling.locatie.asGeolattePoint(), aanzicht.anker.asGeolattePoint(), mapUnitsPerPixel),
      feature => aanzicht.hoek * -1,
      new BasicStroke(Math.round(2 / graphics.getMapUnitsPerPixel))
    )
    painter.paint(PlanarFeature.from(new AanzichtFeature(opstelling, aanzicht)))
  }

  private def verkleinTeGroteImage(image: BufferedImage, mapUnitsPerPixel: Double): BufferedImage = {

    def mapUnitsToPixels(mapUnits: Double, upp: Double): Int = {
      Math.round(mapUnits / upp).toInt
    }

    val maxPixels =
      if (mapUnitsPerPixel >= REFERENTIE_UPP) {
        // maxBordImageGrootteInMeter geldt voor zoomniveau met UPP >= 0.125 (verder uitgezoomd)
        mapUnitsToPixels(maxBordImageGrootteInMeter, mapUnitsPerPixel)
      } else {
        // voor diepere zoomniveau's behouden we het aantal pixels dat maxBordImageGrootteInMeter is bij 0.125
        mapUnitsToPixels(maxBordImageGrootteInMeter, REFERENTIE_UPP)
      }

    val origineleBreedte = image.getWidth
    val origineleHoogte  = image.getHeight

    if (origineleBreedte > 0 && origineleHoogte > 0 && (origineleBreedte > maxPixels || origineleHoogte > maxPixels)) {
      val breedte = Math.min(origineleBreedte, maxPixels)
      val hoogte  = Math.min(origineleHoogte, maxPixels)

      val xSchaling = breedte / origineleBreedte.toFloat
      val ySchaling = hoogte / origineleHoogte.toFloat

      // kies de kleinste schaling en gebruik die voor x en y om de breedte-hoogte verhouding niet te wijzigen
      val schalingBehoudVerhouding = Math.min(xSchaling, ySchaling)

      val nieuweBreedteBehoudVerhouding = Math.round(schalingBehoudVerhouding * origineleBreedte)
      val nieuweHoogteBehoudVerhouding  = Math.round(schalingBehoudVerhouding * origineleHoogte)

      val raster      = image.getData.createCompatibleWritableRaster(nieuweBreedteBehoudVerhouding, nieuweHoogteBehoudVerhouding)
      val nieuweImage = new BufferedImage(image.getColorModel, raster, image.isAlphaPremultiplied, null)

      val graphics2D = nieuweImage.getGraphics.asInstanceOf[Graphics2D]
      graphics2D.scale(schalingBehoudVerhouding, schalingBehoudVerhouding)
      graphics2D.drawImage(image, 0, 0, null)
      graphics2D.dispose()

      nieuweImage
    } else {
      image
    }
  }

  private def verzekerMaxAfstandAnkerpunt(opstelling: Point[C2D], ankerpunt: Point[C2D], mapUnitsPerPixel: Double): Point[C2D] = {

    val schaalTov125  = mapUnitsPerPixel / REFERENTIE_UPP

    val zoomNiveauMaximumAfstandInMeter =
      if (mapUnitsPerPixel < REFERENTIE_UPP) {
        MAX_AFSTAND_ANKERPUNT * schaalTov125
      } else {
        MAX_AFSTAND_ANKERPUNT
      }

    val afstandInMeter = new JTSGeometryOperations().distance(opstelling, ankerpunt)

    val geschaaldeAfstand =
      if (mapUnitsPerPixel < REFERENTIE_UPP) {
        // altijd herschalen
        afstandInMeter * schaalTov125
      } else {
        // niet herschalen
        afstandInMeter
      }

    val teGebruikenAfstand = Math.min(geschaaldeAfstand, zoomNiveauMaximumAfstandInMeter)

    if (teGebruikenAfstand < afstandInMeter) {

      // https://stackoverflow.com/questions/2353268/java-2d-moving-a-point-p-a-certain-distance-closer-to-another-point

      val opstellingJts = new Coordinate(opstelling.getPosition.getX, opstelling.getPosition.getY)
      val aanzichtJts   = new Coordinate(ankerpunt.getPosition.getX, ankerpunt.getPosition.getY)

      val vectorOpMaxAfstand = Vector2D.create(opstellingJts, aanzichtJts).normalize().multiply(teGebruikenAfstand)
      val puntOpMaxAfstand   = Vector2D.create(opstellingJts).add(vectorOpMaxAfstand)

      new Point(new C2D(puntOpMaxAfstand.getX, puntOpMaxAfstand.getY), CRS.LAMBERT72)

    } else {
      ankerpunt
    }
  }

}

object OpstellingImagePainter {
  val maxBordImageGrootteInMeter = 50
}
