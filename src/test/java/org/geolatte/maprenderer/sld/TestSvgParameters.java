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

import net.opengis.se.v_1_1_0.FillType;
import net.opengis.se.v_1_1_0.StrokeType;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/2/11
 */
public class TestSvgParameters {

     final private static String stroke =
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

    final private static String emptyStroke =
            "<Stroke  version=\"1.1.0\"" +
            "                  xmlns=\"http://www.opengis.net/se\"" +
            "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
            "</Stroke>";

    final private static String fill =
            "<Fill  version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "    <SvgParameter name=\"fill\">\n#FF0000\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"fill-opacity\">0.6</SvgParameter>" +
            "</Fill>";

    final private static String emptyFill =
            "<Fill  version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
            "</Fill>";




    private SvgParameters strokeParameters;
    private SvgParameters strokeDefaultParameters;
    private SvgParameters fillParameters;
    private SvgParameters fillDefaultParameters;

    @Before
    public void setUp() {
        StrokeType strokeType = SLD.instance().read(stroke, StrokeType.class);
        strokeParameters = SvgParameters.create(strokeType.getSvgParameter());
        strokeType = SLD.instance().read(emptyStroke, StrokeType.class);
        strokeDefaultParameters = SvgParameters.create(strokeType.getSvgParameter());
        FillType fillType = SLD.instance().read(fill, FillType.class);
        fillParameters = SvgParameters.create(fillType.getSvgParameter());
        fillType = SLD.instance().read(emptyFill, FillType.class);
        fillDefaultParameters = SvgParameters.create(fillType.getSvgParameter());
    }

    @Test
    public void testStrokeColor() {
        assertEquals(Color.RED, strokeParameters.getStrokeColor());
    }

     @Test
    public void testDefaultStrokeColor() {
        assertEquals(SvgParameters.DEFAULT_STROKE_COLOR, strokeDefaultParameters.getStrokeColor());
    }

    @Test
    public void testStrokeWidth(){
        assertEquals(2f, strokeParameters.getStrokeWidth(), 0.00001);
    }

    @Test
    public void testDefaultStrokeWidth(){
        assertEquals(SvgParameters.DEFAULT_STROKE_WIDTH, strokeDefaultParameters.getStrokeWidth(), 0.00001);
    }

    @Test
    public void testStrokeOpacity(){
        assertEquals(0.5f, strokeParameters.getStrokeOpacity(), 0.00001f);
    }

    @Test
    public void testDefaultStrokeOpacity() {
        assertEquals(SvgParameters.DEFAULT_STROKE_OPACITY, strokeDefaultParameters.getStrokeOpacity(), 0.0001f);
    }

    @Test
    public void testStrokeLinejoin() {
        assertEquals(BasicStroke.JOIN_ROUND, strokeParameters.getStrokeLinejoin());
    }

    @Test
    public void testDefaultStrokeLinejoin() {
        assertEquals(SvgParameters.DEFAULT_STROKE_LINEJOIN, strokeDefaultParameters.getStrokeLinejoin());
    }

    @Test
    public void testStrokeLinecap() {
        assertEquals(BasicStroke.CAP_BUTT, strokeParameters.getStrokeLinecap());
    }

    @Test
    public void testDefaultStrokeLinecap() {
        assertEquals(SvgParameters.DEFAULT_STROKE_LINECAP, strokeDefaultParameters.getStrokeLinecap());
    }

    @Test
    public void testStrokeDasharray(){
        float[] dashArray = strokeParameters.getStrokeDasharray();
        assertEquals(6, dashArray.length);
        assertEquals(1f, dashArray[0], 0.00001f);
        assertEquals(2f, dashArray[1], 0.00001f);
        assertEquals(3f, dashArray[2], 0.00001f);
        assertEquals(1f, dashArray[3], 0.00001f);
        assertEquals(2f, dashArray[4], 0.00001f);
        assertEquals(3f, dashArray[5], 0.00001f);
    }

    @Test
    public void testDefaultStrokeDasharray(){
        float[] dashArray = strokeDefaultParameters.getStrokeDasharray();
        assertNull(dashArray);
    }

    @Test
    public void testStrokeDashoffset() {
        assertEquals(2f, strokeParameters.getStrokeDashoffset(), 0.00001f);
    }

    @Test
    public void testDefaultStrokeDashoffset() {
        assertEquals(SvgParameters.DEFAULT_STROKE_DASHOFFSET, strokeDefaultParameters.getStrokeDashoffset(), 0.0000f);
    }

    @Test
    public void testFillColor() {
        assertEquals(Color.RED, fillParameters.getFillColor());
    }

    @Test
    public void testDefaultFillColor() {
        assertEquals(SvgParameters.DEFAULT_FILL_COLOR, fillDefaultParameters.getFillColor());
    }

    @Test
    public void testFillOpacity() {
        assertEquals(0.6f, fillParameters.getFillOpacity(), 0.00001f);
    }

    @Test
    public void testDefaultFillOpacity() {
        assertEquals(SvgParameters.DEFAULT_FILL_OPACITY, fillDefaultParameters.getFillOpacity(),0.00001f);
    }

}