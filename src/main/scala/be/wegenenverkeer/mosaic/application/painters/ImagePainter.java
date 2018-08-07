package be.wegenenverkeer.mosaic.application.painters;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Feature;
import org.geolatte.geom.Point;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.maprenderer.painters.EmbeddedImagePainter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * Tekent een gecentreerde image (ipv top left) voor opstelling hoek
 */
public class ImagePainter implements Painter {

	final private static Logger logger = LoggerFactory.getLogger( EmbeddedImagePainter.class );

	final private MapGraphics graphics;
	final private ImageExtractor imageExtractor;

	/**
	 * Constructs an instance
	 *
	 * @param graphics the graphics to use
	 * @param imageExtractor specifies how to get the image from the feature
	 */
	public ImagePainter(MapGraphics graphics, ImageExtractor imageExtractor) {
		this.graphics = graphics;
		this.imageExtractor = imageExtractor;
	}

	/**
	 * Paints the feature
	 *
	 * @param feature the feature to paint
	 */
	public void paint(PlanarFeature feature) {
		Point<C2D> point = (Point<C2D>) feature.getGeometry();
		Optional<BufferedImage> imageOpt = imageExtractor.get( feature );
		if ( !imageOpt.isPresent() ) {
			return;
		}
		BufferedImage image = imageOpt.get();
		C2D pos = point.getPosition();
    AffineTransform currentTransform = graphics.getTransform();
    try {
      graphics.setTransform( new AffineTransform() );
      Point2D anchor = doTransform( pos, mkTransform( currentTransform, image ) );
      graphics.drawImage( image, (int) anchor.getX(), (int) anchor.getY(), null );
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

	private AffineTransform mkTransform(AffineTransform currentTransform, BufferedImage image) {
		AffineTransform shiftLowerCenter = new AffineTransform();
		shiftLowerCenter.setToTranslation( -image.getWidth()/2, ( -image.getHeight()/2) );
		shiftLowerCenter.concatenate( currentTransform );
		return shiftLowerCenter;
	}

	/**
	 * Given a Feature, extract the Image
	 */
	public interface ImageExtractor {
		Optional<BufferedImage> get(Feature feature);
	}
}
