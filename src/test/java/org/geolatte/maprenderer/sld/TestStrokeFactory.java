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
import static org.junit.Assert.assertNull;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/8/11
 */
public class TestStrokeFactory {

    final private static String strokeFragment =
                        "<Stroke  version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "    <SvgParameter name=\"stroke\">\n#FF0000\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"stroke-width\">2</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-opacity\">0.5</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-linejoin\">round</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-linecap\">butt</SvgParameter>" +
                        "</Stroke>";

     final private static String dashedStrokeFragment =
                        "<Stroke  version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "    <SvgParameter name=\"stroke\">\n#FF0000\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"stroke-width\">2</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-opacity\">0.5</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-linejoin\">round</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-linecap\">butt</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-dasharray\">1.0 2.0 3.0</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-dashoffset\">2.0</SvgParameter>" +
                        "</Stroke>";


    private BasicStroke stroke;
    private BasicStroke dashedStroke;
    private StrokeFactory strokeFactory;

    @Before
    public void setUp() {
        strokeFactory  = new StrokeFactory();
        StrokeType strokeType = SLD.instance().read(strokeFragment, StrokeType.class);
        stroke = (BasicStroke)strokeFactory.create(SvgParameters.create(strokeType.getSvgParameter()));
        strokeType = SLD.instance().read(dashedStrokeFragment, StrokeType.class);
        dashedStroke = (BasicStroke)strokeFactory.create(SvgParameters.create(strokeType.getSvgParameter()));
    }


    @Test
    public void testStrokeWidth(){
        assertEquals(2,stroke.getLineWidth(), 0.00000001);
    }

    @Test
    public void testLinejoin(){
        assertEquals(BasicStroke.JOIN_ROUND, stroke.getLineJoin());
    }

    @Test
    public void testLinecap(){
        assertEquals(BasicStroke.CAP_BUTT, stroke.getEndCap());
    }

    @Test
    public void testStrokeDashArray() {
        assertNull(stroke.getDashArray());
    }

    @Test
    public void testStrokeDashOffset() {
        assertEquals(0.0f, stroke.getDashPhase(), 0.00001f);
    }

    @Test
    public void testDashedStrokeDashArray() {
        assertEquals(6, dashedStroke.getDashArray().length);
    }

    @Test
    public void testDashedStrokeDashOffset() {
        assertEquals(2.0f, dashedStroke.getDashPhase(), 0.00001f);
    }


}
