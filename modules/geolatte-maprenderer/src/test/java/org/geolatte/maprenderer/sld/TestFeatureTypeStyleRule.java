/*
 * This file is part of the GeoLatte project.
 *
 *     GeoLatte is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GeoLatte is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with GeoLatte.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (C) 2010 - 2011 and Ownership of code is shared by:
 *  Qmino bvba - Esperantolaan 4 - 3001 Heverlee  (http://www.qmino.com)
 *  Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.maprenderer.sld;

import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.sld.filter.AlwaysTrueSLDRuleFilter;
import org.geolatte.maprenderer.sld.filter.ElseSLDRuleFilter;
import org.geolatte.maprenderer.sld.filter.SLDRuleFilter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestFeatureTypeStyleRule extends BaseFeatureTypeStyleTest {

    @Before
    public void before() {
        super.before();
    }

    @Test
    public void testScaleDenominatorPresentInRule0() {
        Rule rule = getFeatureTypeStyle().getRules().get(0);
        assertEquals(Double.valueOf(100d), rule.getMinScaleDenominator());
        assertEquals(Double.valueOf(1000d), rule.getMaxScaleDenominator());
    }

    @Test
    public void testNoScaleDenominatorPresintInRule1() {
        Rule rule = getFeatureTypeStyle().getRules().get(1);
        assertEquals(Double.valueOf(0.d), rule.getMinScaleDenominator());
        assertEquals(Double.valueOf(Double.POSITIVE_INFINITY), rule.getMaxScaleDenominator());
    }

    @Test
    public void testRuleAlwaysHasFilter() {
        List<Rule> rules = getFeatureTypeStyle().getRules();
        for (Rule rule : rules) {
            SLDRuleFilter filter = rule.getFilter();
            assertNotNull(filter);
        }
        assertTrue(rules.get(1).getFilter() instanceof AlwaysTrueSLDRuleFilter);
        assertTrue(rules.get(2).getFilter() instanceof ElseSLDRuleFilter);
    }

    @Test
    public void testRules0Has4Symbolizers() {
        Rule rule = getFeatureTypeStyle().getRules().get(0);
        assertEquals(4, rule.getSymbolizers().size());

    }

    @Test
    public void testSymbolizerListIsCopy(){
        Rule rule = getFeatureTypeStyle().getRules().get(0);
        List<AbstractSymbolizer> symbolizers = rule.getSymbolizers();
        int originalSize = symbolizers.size();
        symbolizers.remove(0);
        symbolizers = rule.getSymbolizers();
        assertEquals(originalSize, symbolizers.size());
    }

    @Test
    public void testScaleDenominatorWithinBounds() {
        Rule rule = getFeatureTypeStyle().getRules().get(0);
        //this assumes the standard pixel size of 0.28x0.28mm!!
        MapGraphics g = createMapGraphics(100, 2.8d);
        assertTrue(rule.withinScaleBounds(g));
        g = createMapGraphics(100, 14d);
        assertTrue(rule.withinScaleBounds(g));
    }

    @Test
    public void testScaleDenominatorNotWithinBounds() {
        Rule rule = getFeatureTypeStyle().getRules().get(0);
        //this assumes the standard pixel size of 0.28x0.28mm
        MapGraphics g = createMapGraphics(1000, 290d);
        assertFalse(rule.withinScaleBounds(g));
        g = createMapGraphics(1000, 560d);
        assertFalse(rule.withinScaleBounds(g));
    }

}
