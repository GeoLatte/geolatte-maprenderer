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

package org.geolatte.maprenderer.sld.graphics;

import net.opengis.se.v_1_1_0.MarkType;
import org.geolatte.maprenderer.sld.SLD;
import org.geolatte.maprenderer.sld.SvgParameters;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/19/11
 */
public class TestMark {

    Mark wellKnownMark;

    @Before
    public void before() {
        MarkType type = SLD.instance().read(xmlWellKnownMark, MarkType.class);
        wellKnownMark = new Mark(type);
    }

    @Test
    public void testWellKnownName(){
        assertEquals("circle", wellKnownMark.getWellKnownName());
    }

    @Test
    public void testHasNoStroke() {
        assertFalse(wellKnownMark.hasStroke());
    }

    @Test
    public void testHasFill() {
        assertTrue(wellKnownMark.hasFill());
    }

    @Test
    public void testEmptyMark() {
        MarkType t = SLD.instance().read(emptyMark, MarkType.class);
        Mark empty = new Mark(t);
        assertEquals("square", empty.getWellKnownName());
        assertTrue(empty.hasFill());
        assertTrue(empty.hasStroke());
        assertEquals(SvgParameters.DEFAULT_FILL_COLOR, empty.getSvgParameters().getFillColor());
        assertEquals(SvgParameters.DEFAULT_STROKE_WIDTH, empty.getSvgParameters().getStrokeWidth(), 0.00001f);
    }

    private final String xmlWellKnownMark =
             "<Mark version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
                    "<WellKnownName>circle</WellKnownName>" +
                    "<Fill>" +
                        "<SvgParameter name=\"fill\">#FF0000</SvgParameter> " +
                        "<SvgParameter name=\"fill-opacity\">0.75</SvgParameter> " +
                    "</Fill>" +
            "</Mark>";

    private final String emptyMark =
             "<Mark version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\"/>";
}
