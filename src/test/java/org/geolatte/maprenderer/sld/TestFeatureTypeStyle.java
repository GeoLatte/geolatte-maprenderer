package org.geolatte.maprenderer.sld;

import net.opengis.se.v_1_1_0.FeatureTypeStyleType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class TestFeatureTypeStyle extends SLDPainterTest {


    @Test
    public void test_constructor(){
        FeatureTypeStyle style = new FeatureTypeStyle(sldRoot);
        assertNotNull(style);
        assertEquals("TEST FEATURETYPESTYLE", style.getName());

    }

    @Test
    public void test_create_painter(){
        FeatureTypeStylePainter painter = featureTypeStyle.painter();
        assertNotNull(painter);
        assertNotNull(painter.getRulePainters());
        assertEquals(2, painter.getRulePainters().size());
    }

    

}
