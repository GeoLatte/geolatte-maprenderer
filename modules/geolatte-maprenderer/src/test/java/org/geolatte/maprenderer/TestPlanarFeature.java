package org.geolatte.maprenderer;

import org.geolatte.geom.Feature;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.test.MockLineStringFeature;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Karel Maesen, Geovise BVBA on 25/07/2018.
 */
public class TestPlanarFeature {

    @Test
    public void testFactoryMethod(){
        Feature<?, ?> testFeature = new MockLineStringFeature();
        PlanarFeature pFeature = PlanarFeature.from(testFeature);

        assertEquals(testFeature.getGeometry(), pFeature.getGeometry());
    }
}
