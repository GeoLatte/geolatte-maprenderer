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
import org.geolatte.geom.jts.JTS;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.shape.ShapeAdapter;
import org.geolatte.test.MockLineStringFeature;
import org.geolatte.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.RenderedImage;

import static org.geolatte.test.TestSupport.assertImageEquals;
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
                        "    <SvgParameter name=\"stroke\">\n#FF0000\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"stroke-width\">2</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-opacity\">.9</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-linejoin\">round</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-linecap\">butt</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-dasharray\">1.0 2.0 3.0</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-dashoffset\">2.0</SvgParameter>" +
                        "</Stroke>" +
                        "<PerpendicularOffset>\n" +
                            " <ogc:Literal>\n20\n</ogc:Literal>\n" +
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
        assertEquals(Float.valueOf(20f), perOffset.value());
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
        LineSymbolizer symbolizer = new LineSymbolizer(type);
        assertNull(symbolizer.getGeometryProperty());
    }

    @Test
    public void testSymbolize() throws Exception {
        MapGraphics g = createMapGraphics(100, 100000);

        //a horizontal line in the middle of the image.
        MockLineStringFeature feature = MockLineStringFeature.createLine(10000d, 50000d, 90000d, 50000d);
        ShapeAdapter adapter = new ShapeAdapter(g.getTransform());
        lineSymbolizer.symbolize(g, JTS.to(feature.getGeometry()));
        RenderedImage img = g.createRendering();
        TestSupport.writeImageToDisk(img, "lineSymbolizer-1.png", "PNG");
        assertImageEquals("expected-lineSymbolizer-1.png", img);
    }

}
