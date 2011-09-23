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

import net.opengis.se.v_1_1_0.PolygonSymbolizerType;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.geolatte.maprenderer.shape.ShapeAdapter;
import org.geolatte.test.MockPolygonFeature;
import org.geolatte.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.io.IOException;

import static org.geolatte.test.TestSupport.assertImageEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/11/11
 */
public class TestPolygonSymbolizer extends BaseFeatureTypeStyleTest {

    PolygonSymbolizer symbolizer;
    PolygonSymbolizerType type;

    @Before
    public void before(){
        super.before();
        type = SLD.instance().read(xmlSymbolizer, PolygonSymbolizerType.class);
        symbolizer = new PolygonSymbolizer(type);
    }

    @Test
    public void testGetGeometryProperty() {
        assertEquals("centerline", symbolizer.getGeometryProperty());
    }


    @Test
    public void test_default_uom_is_pixel() {
        assertEquals(UOM.PIXEL, symbolizer.getUOM());
    }

    @Test
    public void test_perpendicularOffset(){
        Value<Float> perOffset = symbolizer.getPerpendicularOffset();
        assertEquals(UOM.PIXEL, perOffset.uom());
        assertEquals(Float.valueOf(10f), perOffset.value());
    }

    @Test
    public void testDisplacement(){
        assertEquals(new Point2D.Float(5f, 10f), symbolizer.getDisplacement());
    }

    @Test
    public void testSymbolizeSimple() throws Exception {
        type = SLD.instance().read(simpleSymbolizer, PolygonSymbolizerType.class);
        testCase("polygonSymbolizer-simple.png");
    }

    @Test
    public void testSymbolizeNoFill() throws Exception {
        type = SLD.instance().read(noFillSymbolizer, PolygonSymbolizerType.class);
        testCase("polygonSymbolizer-nofill.png");
    }

    @Test
    public void testSymbolizeNoStroke() throws Exception {
        type = SLD.instance().read(noStrokeSymbolizer, PolygonSymbolizerType.class);
        testCase("polygonSymbolizer-nostroke.png");
    }

    //TODO -- add test case for polygon with perpendicular offset

    //TODO -- add test case for polygon with displacement

    //TODO -- add test case for donut polygon

    private void testCase(String testCaseName) throws SpatialReferenceCreationException, IOException {
       symbolizer = new PolygonSymbolizer(type);

        MapGraphics g = createMapGraphics(100, 10000);

        //a horizontal line in the middle of the image.
        MockPolygonFeature feature = MockPolygonFeature.createRect(3000, 3000, 6000, 6000);
        ShapeAdapter adapter = new ShapeAdapter(g.getTransform());
        symbolizer.symbolize(g, feature.getGeometry());
        RenderedImage img = g.createRendering();
        TestSupport.writeImageToDisk(img, testCaseName, "PNG");
        assertImageEquals("expected-" + testCaseName, img);
    }


    @Test
    public void testIllegalSymbolizerSpecification(){
        String test =  "<PolygonSymbolizer version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "<Geometry>\n" +
                         "    <ogc:PropertyName>\ncenterline\n</ogc:PropertyName>\n" +
                        "</Geometry>" +
                        "<Fill>" +
                            "<SvgParameter name=\"fill\">#00FF00</SvgParameter> " +
                        "</Fill>" +
                        "<Displacement>" +
                        "   <DisplacementX> hunderd pixels </DisplacementX>" +
                        "   <DisplacementY>10.0</DisplacementY>" +
                        "</Displacement>" +
                        "<Stroke>" +
                        "    <SvgParameter name=\"stroke\">\n#FF0000\n</SvgParameter>\n" +
                        "</Stroke>" +
                        "<PerpendicularOffset>\n" +
                            "10\n" +
                        "</PerpendicularOffset>"+
             "</PolygonSymbolizer>";
            type = SLD.instance().read(test, PolygonSymbolizerType.class);
        try {
            symbolizer = new PolygonSymbolizer(type);
            fail();
        } catch (Exception e) {
            //OK
        }
    }

       String xmlSymbolizer =
            "<PolygonSymbolizer version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "<Geometry>\n" +
                         "    <ogc:PropertyName>\ncenterline\n</ogc:PropertyName>\n" +
                        "</Geometry>" +
                        "<Fill>" +
                            "<SvgParameter name=\"fill\">#00FF00</SvgParameter> " +
                            "<SvgParameter name=\"fill-opacity\">0.6</SvgParameter> " +
                        "</Fill>" +
                        "<Displacement>" +
                        "   <DisplacementX>5.0</DisplacementX>" +
                        "   <DisplacementY>10.0</DisplacementY>" +
                        "</Displacement>" +
                        "<Stroke>" +
                        "    <SvgParameter name=\"stroke\">\n#FF0000\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"stroke-width\">1</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-opacity\">1</SvgParameter>" +
                        "</Stroke>" +
                        "<PerpendicularOffset>\n" +
                            "10\n" +
                        "</PerpendicularOffset>"+
             "</PolygonSymbolizer>";

    String simpleSymbolizer =
            "<PolygonSymbolizer version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "<Geometry>\n" +
                         "    <ogc:PropertyName>\ncenterline\n</ogc:PropertyName>\n" +
                        "</Geometry>" +
                        "<Fill>" +
                            "<SvgParameter name=\"fill\">#00FF00</SvgParameter> " +
                            "<SvgParameter name=\"fill-opacity\">1.0</SvgParameter> " +
                        "</Fill>" +
                        "<Stroke>" +
                        "    <SvgParameter name=\"stroke\">\n#FF0000\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"stroke-width\">1</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-opacity\">1</SvgParameter>" +
                        "</Stroke>" +
             "</PolygonSymbolizer>";

    String noFillSymbolizer =
            "<PolygonSymbolizer version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "<Geometry>\n" +
                         "    <ogc:PropertyName>\ncenterline\n</ogc:PropertyName>\n" +
                        "</Geometry>" +
                        "<Stroke>" +
                        "    <SvgParameter name=\"stroke\">\n#FF0000\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"stroke-width\">1</SvgParameter>" +
                        "    <SvgParameter name=\"stroke-opacity\">1</SvgParameter>" +
                        "</Stroke>" +
             "</PolygonSymbolizer>";

    String noStrokeSymbolizer =
            "<PolygonSymbolizer version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "<Geometry>\n" +
                         "    <ogc:PropertyName>\ncenterline\n</ogc:PropertyName>\n" +
                        "</Geometry>" +
                        "<Fill>" +
                            "<SvgParameter name=\"fill\">#00FF00</SvgParameter> " +
                            "<SvgParameter name=\"fill-opacity\">0.6</SvgParameter> " +
                        "</Fill>" +
             "</PolygonSymbolizer>";




}
