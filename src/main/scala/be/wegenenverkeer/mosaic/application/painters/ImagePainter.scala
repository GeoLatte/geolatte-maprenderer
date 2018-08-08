package be.wegenenverkeer.mosaic.application.painters
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.util.Optional

import org.geolatte.geom.{ C2D, Feature, Point, Position }
import org.geolatte.maprenderer.map.{ MapGraphics, Painter, PlanarFeature }
import org.geolatte.maprenderer.painters.EmbeddedImagePainter
import org.slf4j.LoggerFactory

class ImagePainter(val graphics: MapGraphics, val imageExtractor: Feature[C2D, Object] => Option[BufferedImage]) extends Painter with Transformer {

  private val logger = LoggerFactory.getLogger(classOf[EmbeddedImagePainter])

  override def paint(feature: PlanarFeature): Unit = {
    val point = feature.getGeometry.asInstanceOf[Point[C2D]]

    imageExtractor(feature).foreach { image =>
      {
        val pos = point.getPosition

        val currentTransform = graphics.getTransform
        try {
          graphics.setTransform(new AffineTransform)
          val anchor = doTransform(pos, mkTransform(currentTransform, image))
          graphics.drawImage(image, anchor.getX.toInt, anchor.getY.toInt, null)
        } catch {
          case t: Throwable =>
            logger.error("Error painting feature", t)
        } finally {
          // restore transform
          graphics.setTransform(currentTransform)
        }

      }
    }
  }

  private def mkTransform(currentTransform: AffineTransform, image: BufferedImage) = {
    val shiftLowerCenter = new AffineTransform
    shiftLowerCenter.setToTranslation(-image.getWidth / 2, -image.getHeight / 2)
    shiftLowerCenter.concatenate(currentTransform)
    shiftLowerCenter
  }
}

object ImagePainter {
  /**
    * Given a Feature, extract the Image
    */
  trait ImageExtractor { def get(feature: Feature[_ <: Position, _]): Optional[BufferedImage] }
}
