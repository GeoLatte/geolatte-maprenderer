package org.geolatte.maprenderer.painters;


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Optional;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Feature;
import org.geolatte.geom.Point;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.maprenderer.sld.PaintFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@Code Painter} that symbolizes the feature using an ambedded image
 * <p>
 * This {@code Painter} will draw an image so that the lower middle point of the image is drawn at an anchor Point
 * (in the Coordinate Space of the geometry), rotated and with a line connecting the Feature (Point)Geometry to the
 * anchor point.
 * <p>
 * Created by Karel Maesen, Geovise BVBA on 06/04/2018.
 */
public class EmbeddedImagePainter implements Painter {

	final private static Logger logger = LoggerFactory.getLogger( EmbeddedImagePainter.class );


	final private MapGraphics graphics;
	final private ImageExtractor imageExtractor;
	final private AnchorPointExtractor anchorPointExtractor;
	final private RotationExtractor rotationExtractor;
	final private Stroke lineStroke;

	/**
	 * Constructs an instance
	 *
	 * @param graphics the graphics to use
	 * @param imageExtractor specifies how to get the image from the feature
	 * @param anchorPointExtractor specifies how to get the anchor point
	 * @param rotationExtractor specifies the angle in radians used to rotate the image
	 * @param lineStroke specifies the stroke to use to render the line connecting the Feature Point to the anchor point
	 */
	public EmbeddedImagePainter(
			MapGraphics graphics,
			ImageExtractor imageExtractor,
			AnchorPointExtractor anchorPointExtractor,
			RotationExtractor rotationExtractor, Stroke lineStroke) {
		this.graphics = graphics;
		this.imageExtractor = imageExtractor;
		this.anchorPointExtractor = anchorPointExtractor;
		this.rotationExtractor = rotationExtractor;
		this.lineStroke = lineStroke;
	}


	public EmbeddedImagePainter(
			MapGraphics graphics,
			ImageExtractor imageExtractor,
			AnchorPointExtractor anchorPointExtractor,
			RotationExtractor rotationExtractor) {
		this( graphics, imageExtractor, anchorPointExtractor, rotationExtractor, new BasicStroke() );

	}

	/**
	 * Paints the feature
	 *
	 * @param feature the feature to paint
	 */
	public void paint(PlanarFeature feature) {
		Point<C2D> point = (Point<C2D>) feature.getGeometry();
		Optional<BufferedImage> imageOpt = imageExtractor.get( feature );
		double rotationAngle = rotationExtractor.get( feature );
		if ( !imageOpt.isPresent() ) {
			return;
		}
		BufferedImage image = imageOpt.get();
		C2D pos = point.getPosition();
		C2D anchor = anchorPointExtractor.get( feature ).getPosition();
		drawLineToAnchor( pos, anchor );
		drawImage( rotationAngle, image, anchor );
//		markPosition( pos );
//		markPosition( anchor);
	}

	/**
	 * Draws a line from feature geometry to the anchor point
	 *
	 * @param posPnt
	 * @param anchorPnt
	 */
	private void drawLineToAnchor(C2D posPnt, C2D anchorPnt) {
		AffineTransform currentTransform = graphics.getTransform();
		try {
			//to use
			graphics.setTransform(new AffineTransform());
			Point2D anchor = doTransform(anchorPnt, currentTransform);
			Point2D pos = doTransform(posPnt, currentTransform);
			graphics.setPaint( new PaintFactory().create( Color.black, 1 ) );
			graphics.setStroke( lineStroke );
			graphics.drawLine( (int) pos.getX(), (int) pos.getY(), (int) anchor.getX(), (int) anchor.getY() );
		}
		catch (Throwable t) {
			logger.error( "Error painting feature", t );
		}
		finally {
			// restore transform
			graphics.setTransform( currentTransform );
		}
	}

	private void drawImage(double rotationAngle, BufferedImage image, C2D anchorPnt) {
		AffineTransform currentTransform = graphics.getTransform();
		try {
			//to use
			graphics.setTransform( new AffineTransform() );
			Point2D anchor = doTransform( anchorPnt, mkTransform( currentTransform, image ) );
			graphics.rotate( rotationAngle, anchor.getX() + image.getWidth() / 2, anchor.getY() + image.getHeight() );
			graphics.drawImage( image, (int) anchor.getX(), (int) anchor.getY(), (ImageObserver) null );
//			graphics.drawRect( (int) anchor.getX(), (int) anchor.getY(), image.getWidth(), image.getHeight() );
		}
		catch (Throwable t) {
			logger.error( "Error painting feature", t );
		}
		finally {
			// restore transform
			graphics.setTransform( currentTransform );
		}
	}

	private void markPosition(C2D point) {
		graphics.setPaint( new PaintFactory().create( Color.black, 1 ) );
		Point2D dstPnt = new java.awt.Point.Double( point.getX(), point.getY() );
		graphics.fillRect( (int) dstPnt.getX(), (int) dstPnt.getY(), 1, 1 );
	}

	private Point2D doTransform(C2D point, AffineTransform transform) {
		return transform.transform( new Point2D.Double( point.getX(), point.getY() ), null );
	}

	private AffineTransform mkTransform(AffineTransform currentTransform, BufferedImage image) {
		AffineTransform shiftLowerCenter = new AffineTransform();
		shiftLowerCenter.setToTranslation( -image.getWidth() / 2, -image.getHeight() );
		shiftLowerCenter.concatenate( currentTransform );
		return shiftLowerCenter;
	}

	/**
	 * Given a Feature, extract the Image
	 */
	public interface ImageExtractor {
		Optional<BufferedImage> get(Feature feature);
	}

	/**
	 * Given a Feature, extract the offset point in the same units and reference system as the Feature Geometry.
	 * <p>
	 * The offset point is where the image will be drawn
	 */
	public interface AnchorPointExtractor {
		Point<C2D> get(Feature feature);
	}

	/**
	 * The Angle in Radians used to rotate the image before rendering the image.
	 */
	public interface RotationExtractor {
		double get(Feature feature);
	}
}
