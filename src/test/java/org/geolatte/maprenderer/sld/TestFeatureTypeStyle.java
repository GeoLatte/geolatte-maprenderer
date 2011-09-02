/*
 * Copyright (c) 2011. Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.maprenderer.sld;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
