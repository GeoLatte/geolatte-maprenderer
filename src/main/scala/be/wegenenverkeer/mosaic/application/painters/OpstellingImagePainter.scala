package be.wegenenverkeer.mosaic.application.painters

import java.awt.BasicStroke

import be.wegenenverkeer.mosaic.domain.model._
import org.geolatte.maprenderer.map.{MapGraphics, Painter, PlanarFeature}
import org.geolatte.maprenderer.painters.EmbeddedImagePainter
import org.geolatte.maprenderer.util.ImageUtils

/**
  *  Tekent een volledige opstelling naar een MapGraphics
  *  Er wordt een PlanarFeature verwacht met onder getProperties.get("properties) een Opstelling
  *
  * if (resolution <= 0.125) {
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
class OpstellingImagePainter(graphics: MapGraphics, resolution: Double) extends Painter {

  override def paint(geojsonPlanarFeature: PlanarFeature): Unit = {
    val opstelling   = geojsonPlanarFeature.getProperties.get("properties").asInstanceOf[Opstelling]
    val opstellingPF = PlanarFeature.from(new OpstellingFeature(opstelling))

    resolution match {
      case res if res <= 0.125 => renderOpstellingMetAanzichten(opstellingPF, opstelling, klein = false)
      case res if res <= 0.25  => renderOpstellingMetAanzichten(opstellingPF, opstelling, klein = true)
      case res if res <= 0.5   => renderOpstellingMetHoek(opstellingPF, opstelling)
      case res                 => renderOpstellingAlsPunt(opstellingPF)
    }

  }

  private def renderOpstellingAlsPunt(planarFeature: PlanarFeature): Unit = {
    val painter = new PointPainter(
      graphics,
      new BasicStroke(Math.round(1.5 / graphics.getMapUnitsPerPixel)),
      3
    )
    painter.paint(planarFeature)
  }

  private def renderOpstellingMetAanzichten(planarFeature: PlanarFeature, opstelling: Opstelling, klein: Boolean): Unit = {
    renderOpstellingMetHoek(planarFeature, opstelling)
    opstelling.aanzichten.foreach(renderAanzicht(_, opstelling, klein))
  }

  private def renderOpstellingMetHoek(planarFeature: PlanarFeature, opstelling: Opstelling): Unit = {
    val painter = new ImagePainter(
      graphics,
      feature => ImageUtils.readImageFromBase64String(opstelling.binaireData.kaartvoorstelling.data)
    )
    painter.paint(planarFeature)
  }

  private def renderAanzicht(aanzicht: Aanzicht, opstelling: Opstelling, klein: Boolean): Unit = {
    val aanzichtImg = ImageUtils.readImageFromBase64String(
      if (klein) aanzicht.binaireData.platgeslagenvoorstellingklein.data  else aanzicht.binaireData.platgeslagenvoorstelling.data
    )

    val painter = new EmbeddedImagePainter(
      graphics,
      feature => aanzichtImg,
      feature => aanzicht.anker.asGeolattePoint(),
      feature => aanzicht.hoek * -1,
      new BasicStroke(Math.round(2 / graphics.getMapUnitsPerPixel))
    )
    painter.paint(PlanarFeature.from(new AanzichtFeature(opstelling, aanzicht)))
  }

}
