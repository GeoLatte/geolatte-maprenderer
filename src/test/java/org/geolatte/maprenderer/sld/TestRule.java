package org.geolatte.maprenderer.sld;

import org.geolatte.maprenderer.sld.filter.AlwaysTrueFilter;
import org.geolatte.maprenderer.sld.filter.ElseFilter;
import org.geolatte.maprenderer.sld.filter.Filter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class TestRule extends SLDPainterTest {


    List<Rule> rules;

    @Before
    public void before(){
        super.before();
        rules = painter.getRules();
    }

    @Test
    public void test_rule_name(){
        Rule rpainter = rules.get(0);
        assertEquals("top", rpainter.getName());
    }

    @Test
    public void test_rule_always_has_filter(){
        for (Rule rule : rules){
            Filter filter = rule.getFilter();
            assertNotNull(filter);
        }
        assertTrue( rules.get(1).getFilter() instanceof AlwaysTrueFilter);
        assertTrue( rules.get(2).getFilter() instanceof ElseFilter);
    }



    @Test
    public void test_rule_hasScaleDenominator(){
        Rule rule = rules.get(0);
        assertEquals(Double.valueOf(100d), rule.getMinScaleDenominator());
        assertEquals(Double.valueOf(1000d), rule.getMaxScaleDenominator());

    }

    
    

}
