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

import net.opengis.se.v_1_1_0.LineSymbolizerType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestLineSymbolizer extends BaseFeatureTypeStyleTest {

    LineSymbolizer lineSymbolizer;
    LineSymbolizerType type;

    public LineSymbolizerType createSymbolizerType() {
        String xmlFragment =
                "<LineSymbolizer version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "<Geometry>\n" +
                        "    <ogc:PropertyName>\ncenterline\n</ogc:PropertyName>\n" +
                        "</Geometry>" +
                        "<Stroke>" +
                        "    <SvgParameter name=\"stroke\">\n#0000F\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"width\">2</SvgParameter>" +
                        "</Stroke>" +
                        "<PerpendicularOffset>\n" +
                            " <ogc:Literal>\n-10\n</ogc:Literal>\n" +
                        "</PerpendicularOffset>"+
                        "</LineSymbolizer>";
        return SLD.instance().read(xmlFragment, LineSymbolizerType.class);
    }

    @Before
    public void before(){
        super.before();
        type = createSymbolizerType();
        lineSymbolizer = new LineSymbolizer(type);
    }

    @Test
    public void test_get_Stroke(){
        //TODO -- complete
    }



    @Test
    public void test_get_geometry_property(){
        assertEquals("centerline", lineSymbolizer.getGeometryProperty());
    }

    @Test
    public void test_default_uom_is_pixel() {
        assertEquals(UOM.PIXEL, lineSymbolizer.getUOM());
    }

    @Test
    public void test_uom_is_foot_or_metre() {
        type.setUom("http://www.opengeospatial.org/se/units/foot");
        lineSymbolizer = new LineSymbolizer(type);
        assertEquals(UOM.FOOT, lineSymbolizer.getUOM());
        type.setUom("http://www.opengeospatial.org/se/units/metre");
        lineSymbolizer = new LineSymbolizer(type);
        assertEquals(UOM.METRE, lineSymbolizer.getUOM());
        
    }

    @Test
    public void test_perpendicularOffset(){
        Value<Float> perOffset = lineSymbolizer.getPerpendicularOffset();
        assertEquals(UOM.PIXEL, perOffset.uom());
        assertEquals(Float.valueOf(-10f), perOffset.value());
    }

    @Test
    public void test_no_geometry() {
        assertEquals("centerline", lineSymbolizer.getGeometryProperty());
        String xmlFragment =
                "<LineSymbolizer version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "<Stroke>" +
                        "    <SvgParameter name=\"stroke\">#0000F</SvgParameter>" +
                        "    <SvgParameter name=\"width\">2</SvgParameter>" +
                        "</Stroke>" +
                        "</LineSymbolizer>";
        LineSymbolizerType type = SLD.instance().read(xmlFragment, LineSymbolizerType.class);
        LineSymbolizer painter = new LineSymbolizer(type);
        assertNull(painter.getGeometryProperty());
    }

}
