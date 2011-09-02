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

import org.geolatte.maprenderer.sld.filter.AlwaysTrueSLDRuleFilter;
import org.geolatte.maprenderer.sld.filter.ElseSLDRuleFilter;
import org.geolatte.maprenderer.sld.filter.SLDRuleFilter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestRule extends SLDPainterTest {


    List<Rule> rules;

    @Before
    public void before(){
        super.before();
        this.rules = getFeatureTypeStyle().getRules();
    }

    @Test
    public void test_rule_name(){
        Rule rpainter = rules.get(0);
        assertEquals("top", rpainter.getName());
    }

    @Test
    public void test_rule_always_has_filter(){
        for (Rule rule : rules){
            SLDRuleFilter filter = rule.getFilter();
            assertNotNull(filter);
        }
        assertTrue( rules.get(1).getFilter() instanceof AlwaysTrueSLDRuleFilter);
        assertTrue( rules.get(2).getFilter() instanceof ElseSLDRuleFilter);
    }



    @Test
    public void test_rule_hasScaleDenominator(){
        Rule rule = rules.get(0);
        assertEquals(Double.valueOf(100d), rule.getMinScaleDenominator());
        assertEquals(Double.valueOf(1000d), rule.getMaxScaleDenominator());

    }

    
    

}
