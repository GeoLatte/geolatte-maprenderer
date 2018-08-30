package org.geolatte.maprenderer.shape;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;

/**
 * Created by Karel Maesen, Geovise BVBA on 06/04/2018.
 */
public class EnvelopeHelper {

	public static double width(Envelope<C2D> env) {
		return env.upperRight().getX() - env.lowerLeft().getX();
	}

	public static double height(Envelope<C2D> env) {
		return env.upperRight().getY() - env.lowerLeft().getY();
	}
}
