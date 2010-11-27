package org.geolatte.maprenderer.sld;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class TestFeatureTypeStyle extends SLDPainterTest {


    @Before
    public void before(){
        super.before();
    }

    @Test
    public void test_constructor(){
        FeatureTypeStyle style = new FeatureTypeStyle(sldRoot);
        assertNotNull(style);
        assertEquals("TEST FEATURETYPESTYLE", style.getName());

    }

    @Test
    public void test_create_painter(){
        FeatureTypeStylePainter painter = featureTypeStyle.createPainter();
        assertNotNull(painter);
        assertNotNull(painter.getRules());
        assertEquals(3, painter.getRules().size());
    }

    

}
