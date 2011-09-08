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

import net.opengis.se.v_1_1_0.StrokeType;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/2/11
 */
public class TestSvgParameters {

     final private static String strokeFragment =
                        "<Stroke  version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "    <SvgParameter name=\"stroke\">\n#FF0000\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"stroke-width\">2</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-opacity\">0.5</SvgParameter>" +
                        "</Stroke>";

    final private static String emptyStroke =
            "<Stroke  version=\"1.1.0\"" +
            "                  xmlns=\"http://www.opengis.net/se\"" +
            "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
            "</Stroke>";



    private SvgParameters parameters;
    private SvgParameters defaultParameters;

    @Before
    public void setUp() {
        StrokeType strokeType = SLD.instance().read(strokeFragment, StrokeType.class);
        parameters = SvgParameters.create(strokeType.getSvgParameter());
        strokeType = SLD.instance().read(emptyStroke, StrokeType.class);
        defaultParameters = SvgParameters.create(strokeType.getSvgParameter());
    }

    @Test
    public void test_read_stroke_color() {
        assertEquals(Color.RED, parameters.getStrokeColor());
    }

     @Test
    public void testDefaultStrokeColor() {
        assertEquals(SvgParameters.DEFAULT_STROKE_COLOR, defaultParameters.getStrokeColor());
    }

    @Test
    public void testStrokeWidth(){
        assertEquals(2f, parameters.getStrokeWidth(), 0.00001);
    }

    @Test
    public void testDefaultStrokeWidth(){
        assertEquals(SvgParameters.DEFAULT_STROKE_WIDTH, defaultParameters.getStrokeWidth(), 0.00001);
    }

    @Test
    public void testStrokeOpacity(){
        assertEquals(0.5f, parameters.getStrokeOpacity(), 0.00001f);
    }

    @Test
    public void testDefaultStrokeOpacity() {
        assertEquals(SvgParameters.DEFAULT_STROKE_OPACITY, defaultParameters.getStrokeOpacity(), 0.0001f);
    }


}
