package be.wegenenverkeer.mosaic.application.painters

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

import org.geolatte.geom.{ C2D, Point }
import org.geolatte.maprenderer.map.{ MapGraphics, Painter, PlanarFeature }
import org.geolatte.maprenderer.painters.EmbeddedImagePainter
import org.slf4j.LoggerFactory

class ImagePainter(val graphics: MapGraphics, val image: BufferedImage) extends Painter with Transformer {

  private val logger = LoggerFactory.getLogger(classOf[EmbeddedImagePainter])

  override def paint(feature: PlanarFeature): Unit = {
    val point            = feature.getGeometry.asInstanceOf[Point[C2D]]
    val pos              = point.getPosition
    val currentTransform = graphics.getTransform
    try {
      graphics.setTransform(new AffineTransform)
      val topLeft = doTransform(pos, currentTransform)

      graphics.drawImage(
        image,
        Math.round(topLeft.getX - image.getWidth / 2).toInt,
        Math.round(topLeft.getY - image.getWidth / 2).toInt,
        null
      )
    } catch {
      case t: Throwable =>
        logger.error("Error painting feature", t)
    } finally {
      // restore transform
      graphics.setTransform(currentTransform)
    }

  }

}
