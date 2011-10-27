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

import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestFeatureTypeStyle extends BaseFeatureTypeStyleTest {


    @Before
    public void before(){
        super.before();
    }

    @Test
    public void testName(){
        assertEquals("TEST FEATURETYPESTYLE", getFeatureTypeStyle().getName());
    }

    @Test
    public void test_createPainter() throws SpatialReferenceCreationException {
        assertNotNull(getFeatureTypeStyle().createPainter(createMapGraphics()));
    }

    @Test
    public void testAllRulesInList() {
        List<Rule> rules = getFeatureTypeStyle().getRules();
        Assert.assertEquals(3, rules.size());
        Assert.assertEquals("top", rules.get(0).getName());
        Assert.assertEquals("bottom", rules.get(1).getName());
        Assert.assertEquals("bottom", rules.get(2).getName());
    }

    @Test
    public void testRulesListIsCopy() {
        List<Rule> rules = getFeatureTypeStyle().getRules();
        rules.remove(0);
        Assert.assertEquals(3, getFeatureTypeStyle().getRules().size());
    }


}
