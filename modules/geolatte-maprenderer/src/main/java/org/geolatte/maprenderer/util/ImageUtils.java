package org.geolatte.maprenderer.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Karel Maesen, Geovise BVBA on 09/04/2018.
 */
public class ImageUtils {

	private final static Logger logger = LoggerFactory.getLogger( ImageUtils.class );

	public static Optional<BufferedImage> readImageFromBase64String(String data) {

		InputStream stream = mkImageInputStream( data );
		try {
			BufferedImage img = ImageIO.read( stream );
			return Optional.of( img );
		}
		catch (IOException e) {
			logger.error( "Failure to read image", e );
			return Optional.empty();
		}
	}

	private static InputStream mkImageInputStream(String data) {
		Base64.Decoder decoder = Base64.getDecoder();
		return new ByteArrayInputStream( decoder.decode( data ) );
	}

}
