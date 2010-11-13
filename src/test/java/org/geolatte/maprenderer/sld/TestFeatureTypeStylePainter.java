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


    

    @Test
    public void test_constructor(){
        assertNotNull(painter);
    }

    /**
     * We need to retrieve painters in reverse order, so that higher-priority
     * elements are rendered last (i.e. on top of other elements).
     */
    @Test
    public void test_get_rules_in_reverse_order(){
        List<RulePainter> rulePainters = painter.getRulePainters();
        assertEquals("bottom", rulePainters.get(0).getName());
        assertEquals("top", rulePainters.get(1).getName());
    }


}
