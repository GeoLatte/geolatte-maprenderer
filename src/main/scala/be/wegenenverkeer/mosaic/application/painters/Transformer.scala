package be.wegenenverkeer.mosaic.application.painters
import java.awt.geom.{AffineTransform, Point2D}

import org.geolatte.geom.C2D

trait Transformer {
  def doTransform(point: C2D, transform: AffineTransform): Point2D =
    transform.transform(new Point2D.Double(point.getX, point.getY), null)

}
