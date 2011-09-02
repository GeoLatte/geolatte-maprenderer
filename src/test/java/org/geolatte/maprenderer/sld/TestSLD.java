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

import net.opengis.se.v_1_1_0.FeatureTypeStyleType;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class TestSLD {

    private static final String TEST_SLD_FILE = "test-sld.xml";
    @Test
    public void test_unmarshal_featuretypestyletype(){        
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEST_SLD_FILE);
        FeatureTypeStyleType style = SLD.instance().unmarshal(in);
        assertNotNull(style);
    }

    @Test
    public void test_create_FeatureTypeStyle(){
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEST_SLD_FILE);
        FeatureTypeStyle style = SLD.instance().create(in);
        assertNotNull(style);
    }
}
