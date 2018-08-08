package be.wegenenverkeer.mosaic.application.painters;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Point;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.maprenderer.painters.EmbeddedImagePainter;
import org.geolatte.maprenderer.sld.PaintFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Tekent een gecentreerde image (ipv top left) voor opstelling hoek
 */
public class PointPainter implements Painter {

	final private static Logger logger = LoggerFactory.getLogger( EmbeddedImagePainter.class );

	final private MapGraphics graphics;

	final private Stroke stroke;

	final private double radius;

	/**
	 * Constructs an instance
	 *
	 * @param graphics the graphics to use
	 */
	public PointPainter(MapGraphics graphics, Stroke stroke, double radius) {
		this.graphics = graphics;
		this.stroke = stroke;
		this.radius = radius;
	}

	/**
	 * Paints the feature
	 *
	 * @param feature the feature to paint
	 */
	public void paint(PlanarFeature feature) {
		Point<C2D> point = (Point<C2D>) feature.getGeometry();
		C2D pos = point.getPosition();
    AffineTransform currentTransform = graphics.getTransform();
    try {
      graphics.setTransform( new AffineTransform() );
      Point2D anchor = doTransform( pos, mkTransform(currentTransform, radius));
      // : {"circle": {"stroke": {"color": "black", "width": 1.5}, "fill": {"color": "black"}, "radius": 3}}
      Ellipse2D circle = new Ellipse2D.Double(anchor.getX(), anchor.getY(), radius * 2, radius * 2);
      graphics.setPaint( new PaintFactory().create( Color.black, 1 ) );
      graphics.setStroke( stroke );
      graphics.fill(circle);
    }
    catch (Throwable t) {
      logger.error( "Error painting feature", t );
    }
    finally {
      // restore transform
      graphics.setTransform( currentTransform );
    }
	}

	private Point2D doTransform(C2D point, AffineTransform transform) {
		return transform.transform( new Point2D.Double( point.getX(), point.getY() ), null );
	}

	private AffineTransform mkTransform(AffineTransform currentTransform, double radius) {
		AffineTransform shiftLowerCenter = new AffineTransform();
		shiftLowerCenter.setToTranslation( -radius/2, ( -radius) );
		shiftLowerCenter.concatenate( currentTransform );
		return shiftLowerCenter;
	}

}
