package be.wegenenverkeer.mosaic.application.painters

import java.awt.geom.{ AffineTransform, Ellipse2D }
import java.awt.{ BasicStroke, Color }

import org.geolatte.geom.{ C2D, Point }
import org.geolatte.maprenderer.map.{ MapGraphics, Painter, PlanarFeature }
import org.geolatte.maprenderer.sld.PaintFactory
import org.slf4j.LoggerFactory

/**
  * Tekent een gecentreerde image (ipv top left) voor opstelling hoek
  */
class PointPainter(val graphics: MapGraphics, val radius: Double) extends Painter with Transformer {

  private final val logger = LoggerFactory.getLogger(classOf[PointPainter])

  override def paint(feature: PlanarFeature): Unit = {
    val point            = feature.getGeometry.asInstanceOf[Point[C2D]]
    val pos              = point.getPosition
    val currentTransform = graphics.getTransform
    try {
      graphics.setTransform(new AffineTransform)
      val anchor = doTransform(pos, currentTransform)

      // {"circle": {"fill": {"color": "black"}, "radius": 3}}
      graphics.setPaint(new PaintFactory().create(Color.black, 1))
      graphics.setStroke(new BasicStroke(0))
      graphics.fill(new Ellipse2D.Double(anchor.getX - radius, anchor.getY - radius, radius * 2, radius * 2))

    } catch {
      case t: Throwable =>
        logger.error("Error painting feature", t)
    } finally {
      // restore transform
      graphics.setTransform(currentTransform)
    }
  }

}
