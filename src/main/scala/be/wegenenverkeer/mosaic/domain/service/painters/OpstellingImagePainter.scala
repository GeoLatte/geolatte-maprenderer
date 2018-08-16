package be.wegenenverkeer.mosaic.domain.service.painters

import java.awt.BasicStroke

import be.wegenenverkeer.mosaic.domain.model._
import be.wegenenverkeer.mosaic.util.Base64Conversion
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.math.Vector2D
import org.geolatte.geom.{ C2D, JTSGeometryOperations, Point }
import org.geolatte.maprenderer.map.{ MapGraphics, Painter, PlanarFeature }
import org.geolatte.maprenderer.painters.EmbeddedImagePainter
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

  def MAX_AFSTAND_ANKERPUNT = 100.0 // maximum afstand tussen opstelling en ankerpunt. Aanzicht wordt dichter gebracht indien groter.

  override def willPaint(): Boolean = {
    graphics.getMapUnitsPerPixel <= 2.0
  }

  override def paint(geojsonPlanarFeature: PlanarFeature): Unit = {

    Option(geojsonPlanarFeature.getProperties.get("properties").asInstanceOf[Opstelling]) match {
      case Some(opstelling) =>
        val opstellingPF = PlanarFeature.from(new OpstellingFeature(opstelling))

        graphics.getMapUnitsPerPixel match {
          case res if res <= 0.125 => renderOpstellingMetAanzichten(opstellingPF, opstelling, klein = false)
          case res if res <= 0.25  => renderOpstellingMetAanzichten(opstellingPF, opstelling, klein = true)
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

  private def renderOpstellingMetAanzichten(planarFeature: PlanarFeature, opstelling: Opstelling, klein: Boolean): Unit = {
    renderOpstellingMetHoek(planarFeature, opstelling)
    opstelling.aanzichten.foreach(renderAanzicht(_, opstelling, klein))
  }

  private def renderOpstellingMetHoek(planarFeature: PlanarFeature, opstelling: Opstelling): Unit = {
    readImageFromBase64String(opstelling.binaireData.kaartvoorstelling.data).foreach { image =>
      val painter = new ImagePainter(graphics, image)
      painter.paint(planarFeature)
    }
  }

  private def renderAanzicht(aanzicht: Aanzicht, opstelling: Opstelling, klein: Boolean): Unit = {
    val painter = new EmbeddedImagePainter(
      graphics,
      feature =>
        ImageUtils.readImageFromBase64String(
          if (klein) aanzicht.binaireData.platgeslagenvoorstellingklein.data else aanzicht.binaireData.platgeslagenvoorstelling.data
      ),
      feature => verzekerMaxAfstandAnkerpunt(opstelling.locatie.asGeolattePoint(), aanzicht.anker.asGeolattePoint()),
      feature => aanzicht.hoek * -1,
      new BasicStroke(Math.round(2 / graphics.getMapUnitsPerPixel))
    )
    painter.paint(PlanarFeature.from(new AanzichtFeature(opstelling, aanzicht)))
  }

  private def verzekerMaxAfstandAnkerpunt(opstelling: Point[C2D], ankerpunt: Point[C2D]): Point[C2D] = {

    if (new JTSGeometryOperations().distance(opstelling, ankerpunt) > MAX_AFSTAND_ANKERPUNT) {

      // https://stackoverflow.com/questions/2353268/java-2d-moving-a-point-p-a-certain-distance-closer-to-another-point

      val opstellingJts = new Coordinate(opstelling.getPosition.getX, opstelling.getPosition.getY)
      val aanzichtJts   = new Coordinate(ankerpunt.getPosition.getX, ankerpunt.getPosition.getY)

      val vectorOpMaxAfstand = Vector2D.create(opstellingJts, aanzichtJts).normalize().multiply(MAX_AFSTAND_ANKERPUNT)
      val puntOpMaxAfstand = Vector2D.create(opstellingJts).add(vectorOpMaxAfstand)

      new Point(new C2D(puntOpMaxAfstand.getX, puntOpMaxAfstand.getY), CRS.LAMBERT72)

    } else {
      ankerpunt
    }
  }

}
