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

import net.opengis.se.v_1_1_0.SvgParameterType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/2/11
 */
public class TestSvgParameter {

    @Test
    public void test_read_svg_parameters() {
        SvgParameterType svgParam = SLD.instance().read(XML_STROKE_WIDTH, SvgParameterType.class);
        SvgParameter param = SvgParameter.create(svgParam);
        assertEquals("stroke", param.getName());
        assertEquals("#0000F", param.getValue());
    }

    public static String XML_STROKE_WIDTH = "<SvgParameter  version=\"1.1.0\"" +
            "                  xmlns=\"http://www.opengis.net/se\"" +
            "                  xmlns:ogc=\"http://www.opengis.net/ogc\" name=\"stroke\">\n#0000F\n</SvgParameter>\n" ;
}
