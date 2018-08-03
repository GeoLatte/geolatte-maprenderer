package org.geolatte.maprenderer.map;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Feature;
import org.geolatte.geom.Geometry;

import java.util.Map;

/**
 * A nicer interface for working with Planar Features.
 *
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public class PlanarFeature implements Feature<C2D, Object> {

    final private Feature<?,?> delegate;

    /**
     * Create a Planer
     * @param f
     * @return
     */
    public static PlanarFeature from(Feature<?,?> f) {
        checkCast(f.getGeometry());
        return new PlanarFeature(f);
    }

    private static void checkCast(Geometry<?> geom) {
        if (!C2D.class.isAssignableFrom( geom.getPositionClass()) ){
            throw new IllegalArgumentException("Features need to be planar (Cartesian Coordinate space)");
        }
    }

    private PlanarFeature(Feature<?,?> delegate){
        this.delegate = delegate;
    }

    @Override
    public Geometry<C2D> getGeometry() {
        return delegate.getGeometry().as(C2D.class);
    }

    @Override
    public Object getId() {
        return delegate.getId();
    }

    @Override
    public Map<String, Object> getProperties() {
        return delegate.getProperties();
    }

}
