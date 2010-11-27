package org.geolatte.maprenderer.sld;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class TestFeatureTypeStylePainter extends SLDPainterTest{


    @Before
    public void before(){
        super.before();
    }

    @Test
    public void test_constructor(){
        assertNotNull(painter);
    }

    @Test
    public void test_get_rules(){
        List<Rule> rules = painter.getRules();
        assertEquals("top", rules.get(0).getName());
        assertEquals("bottom", rules.get(1).getName());
    }


}
