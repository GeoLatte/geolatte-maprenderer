package org.geolatte.maprenderer.sld;

import org.geolatte.maprenderer.sld.filter.Filter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class TestRulePainter extends SLDPainterTest {

    @Test
    public void test_rule_painter_name(){
        RulePainter rpainter = painter.getRulePainters().get(0);
        assertEquals("bottom", rpainter.getName());
    }


    @Test
    public void test_rule_painter_getSymbolizers(){
        RulePainter rpainter = painter.getRulePainters().get(0);
        List<SymbolizerPainter> spainters = rpainter.getSymbolizerPainters();
        assertNotNull(spainters);
        assertEquals(4, spainters);
    }

    @Test
    public void test_rule_getFilters(){
        RulePainter rpainter = painter.getRulePainters().get(0);
        Filter filter = rpainter.getFilter();
        assertNotNull(filter);
    }

}
