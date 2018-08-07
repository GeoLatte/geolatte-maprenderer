package be.wegenenverkeer.mosaic.application.painters

import java.awt.BasicStroke

import be.wegenenverkeer.mosaic.domain.model._
import org.geolatte.maprenderer.map.{ MapGraphics, Painter, PlanarFeature }
import org.geolatte.maprenderer.painters.EmbeddedImagePainter
import org.geolatte.maprenderer.util.ImageUtils

/*
  Tekent een volledige opstelling naar een MapGraphics
  Er wordt een PlanarFeature verwacht met onder getProperties,get("properties) een Opstelling
 */
class OpstellingImagePainter(graphics: MapGraphics) extends Painter {

  override def paint(geojsonPlanarFeature: PlanarFeature): Unit = {
    val opstelling = geojsonPlanarFeature.getProperties.get("properties").asInstanceOf[Opstelling]

    renderOpstellingHoek(PlanarFeature.from(new OpstellingFeature(opstelling)), opstelling)

    opstelling.aanzichten.foreach(renderAanzicht(opstelling, _))
  }

  private def renderOpstellingHoek(planarFeature: PlanarFeature, opstelling: Opstelling): Unit = {
    val painter = new ImagePainter(
      graphics,
      feature => ImageUtils.readImageFromBase64String(opstelling.binaireData.kaartvoorstelling.data)
    )
    painter.paint(planarFeature)
  }

  private def renderAanzicht(opstelling: Opstelling, aanzicht: Aanzicht): Unit = {
    val painter = new EmbeddedImagePainter(
      graphics,
      feature => ImageUtils.readImageFromBase64String(aanzicht.binaireData.platgeslagenvoorstelling.data),
      feature => aanzicht.anker.asGeolattePoint(),
      feature => aanzicht.hoek * -1,
      new BasicStroke(Math.round(2 / graphics.getMapUnitsPerPixel))
    )
    painter.paint(PlanarFeature.from(new AanzichtFeature(opstelling, aanzicht)))
  }

}
