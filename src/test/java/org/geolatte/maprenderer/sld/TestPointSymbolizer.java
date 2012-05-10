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

import net.opengis.se.v_1_1_0.PointSymbolizerType;
import org.geolatte.geom.jts.JTS;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.shape.ShapeAdapter;
import org.geolatte.maprenderer.sld.graphics.Graphic;
import org.geolatte.maprenderer.sld.graphics.MarkOrExternalGraphicHolder;
import org.geolatte.test.MockPointFeature;
import org.geolatte.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;

import static org.geolatte.test.TestSupport.assertImageEquals;
import static org.junit.Assert.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/12/11
 */
public class TestPointSymbolizer extends BaseFeatureTypeStyleTest {

    PointSymbolizer symbolizer;
    PointSymbolizerType type;

    @Before
    public void before() {
        super.before();
        type = SLD.instance().read(xmlPointSymbolizer, PointSymbolizerType.class);
        symbolizer = new PointSymbolizer(type);
    }

    @Test
    public void testGetGeometryProperty() {
        assertEquals("point", symbolizer.getGeometryProperty());
    }

    @Test
    public void testGetGraphics() {
        Graphic graphic = symbolizer.getGraphic();
        assertNotNull(graphic);
        assertEquals(16, graphic.getSize(), 0.00001f);
        assertFalse(graphic.isSizeSet());
        assertEquals(2, graphic.getSources().size());
        MarkOrExternalGraphicHolder holder = graphic.getSources().get(0);
        assertTrue(holder.isExternalGraphic());
        holder = graphic.getSources().get(1);
        assertTrue(holder.isMark());
        assertEquals("square", holder.getMark().getWellKnownName());
    }

    @Test
    public void testSymbolizerCenterPng() throws IOException {
        testCase(symbolizer, "point-bus-center.png");
    }

    @Test
    public void testSymbolizerGraphicPartiallyRenderedPng() throws IOException {
        testCase(symbolizer, 10000d, 5000d, "point-bus-right.png");
    }

    @Test
    public void testAnchorPointToLowerLeft() throws IOException {
        symbolizer = getSymbolizer(xmlLowerLeftAnchorPoint);
        testCase(symbolizer, "point-bus-anchorpoint-lower-left.png");
    }

    @Test
    public void testAnchorPointToUpperRight() throws IOException {
        symbolizer = getSymbolizer(xmlUpperRightAnchorPoint);
        testCase(symbolizer, "point-bus-anchorpoint-upper-right.png");
    }

    @Test
    public void testDisplacement() throws IOException {
        symbolizer = getSymbolizer(xmlDisplacement);
        testCase(symbolizer, "point-bus-displacement.png");
    }

    @Test
    public void testSymbolizeSvg() throws IOException {
        symbolizer = getSymbolizer(xmlSVGGraphic);
        testCase(symbolizer, "point-info-svg.png");
    }

    @Test
    public void testScalePNG() throws IOException {
        symbolizer = getSymbolizer(xmlScale);
        assertEquals(74f, symbolizer.getGraphic().getSize(), 0.0001f);
        testCase(symbolizer, "point-bus-scale.png");
    }

    @Test
    public void testRotatePNG() throws IOException {
        symbolizer = getSymbolizer(xmlRotate);
        assertEquals(90f, symbolizer.getGraphic().getRotation(), 0.0001f);
        testCase(symbolizer, "point-bus-rotate.png");
    }

    @Test
    public void testRotateSVG() throws IOException {
        symbolizer = getSymbolizer(xmlSVGGraphicRotate);
        assertEquals(90f, symbolizer.getGraphic().getRotation(), 0.0001f);
        testCase(symbolizer, "point-info-svg-rotate.png");
    }

    @Test
    public void testOpacityPNG() throws IOException {
        symbolizer = getSymbolizer(xmlOpacity);
        assertEquals(0.5f, symbolizer.getGraphic().getOpacity(), 0.00001f);
        testCase(symbolizer, "point-bus-opacity.png");
    }


    private void testCase(PointSymbolizer symbolizer, double x, double y, String testCaseName) throws IOException {
        MapGraphics g = createMapGraphics(100, 10000);
        MockPointFeature feature = MockPointFeature.createPoint(x, y);
        //a horizontal line in the middle of the image.
        ShapeAdapter adapter = new ShapeAdapter(g.getTransform());
        AffineTransform originalTransform = g.getTransform();
        symbolizer.symbolize(g, JTS.to(feature.getGeometry()));
        assertEquals("Test that symbolizer restores always the original transform", originalTransform, g.getTransform());
        RenderedImage img = g.createRendering();
        TestSupport.writeImageToDisk(img, testCaseName, "PNG");
        assertImageEquals("expected-" + testCaseName, img);
    }


    private void testCase(PointSymbolizer symbolizer, String testCaseName) throws IOException {
        testCase(symbolizer, 5000d, 5000d, testCaseName);
    }

    private PointSymbolizer getSymbolizer(String name) {
        PointSymbolizerType type = SLD.instance().read(name, PointSymbolizerType.class);
        return new PointSymbolizer(type);
    }

    String xmlPointSymbolizer =
            "<PointSymbolizer version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                    "<Geometry>\n" +
                    "    <ogc:PropertyName>\npoint\n</ogc:PropertyName>\n" +
                    "</Geometry>" +
                    "<Graphic>" +
                    "<ExternalGraphic>" +
                    "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/bus.png\"/>" +
                    "<Format>image/png</Format>" +
                    "</ExternalGraphic>" +
                    "<Mark/>" +
                    "</Graphic>" +
                    "</PointSymbolizer>";

    String xmlLowerLeftAnchorPoint = "<PointSymbolizer version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                    "<Geometry>\n" +
                    "    <ogc:PropertyName>\npoint\n</ogc:PropertyName>\n" +
                    "</Geometry>" +
                    "<Graphic>" +
                    "<ExternalGraphic>" +
                    "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/bus.png\"/>" +
                    "<Format>image/png</Format>" +
                    "</ExternalGraphic>" +
                    "<AnchorPoint>" +
                        "<AnchorPointX>0</AnchorPointX>" +
                        "<AnchorPointY>0</AnchorPointY>" +
                    "</AnchorPoint>" +
                    "</Graphic>" +
                    "</PointSymbolizer>";

        String xmlUpperRightAnchorPoint = "<PointSymbolizer version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                    "<Geometry>\n" +
                    "    <ogc:PropertyName>\npoint\n</ogc:PropertyName>\n" +
                    "</Geometry>" +
                    "<Graphic>" +
                    "<ExternalGraphic>" +
                    "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/bus.png\"/>" +
                    "<Format>image/png</Format>" +
                    "</ExternalGraphic>" +
                    "<AnchorPoint>" +
                        "<AnchorPointX>1.0</AnchorPointX>" +
                        "<AnchorPointY>1.0</AnchorPointY>" +
                    "</AnchorPoint>" +
                    "</Graphic>" +
                    "</PointSymbolizer>";

        String xmlDisplacement = "<PointSymbolizer version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                    "<Geometry>\n" +
                    "    <ogc:PropertyName>\npoint\n</ogc:PropertyName>\n" +
                    "</Geometry>" +
                    "<Graphic>" +
                    "<ExternalGraphic>" +
                    "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/bus.png\"/>" +
                    "<Format>image/png</Format>" +
                    "</ExternalGraphic>" +
                    "<Displacement>" +
                        "<DisplacementX>10</DisplacementX>" +
                        "<DisplacementY>20</DisplacementY>" +
                    "</Displacement>" +
                    "</Graphic>" +
                    "</PointSymbolizer>";

        String xmlSVGGraphic = "<PointSymbolizer version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                    "<Geometry>\n" +
                    "    <ogc:PropertyName>\npoint\n</ogc:PropertyName>\n" +
                    "</Geometry>" +
                    "<Graphic>" +
                    "<ExternalGraphic>" +
                    "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/information.svg\"/>" +
                    "<Format>image/svg+xml</Format>" +
                    "</ExternalGraphic>" +
                    "<Size>10</Size>" +
                    "</Graphic>" +
                    "</PointSymbolizer>";

        String xmlScale =
                "<PointSymbolizer version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                    "<Geometry>\n" +
                    "    <ogc:PropertyName>\npoint\n</ogc:PropertyName>\n" +
                    "</Geometry>" +
                    "<Graphic>" +
                    "<ExternalGraphic>" +
                    "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/bus.png\"/>" +
                    "<Format>image/png</Format>" +
                    "</ExternalGraphic>" +
                    "<Size>74</Size>" +
                    "</Graphic>" +
                    "</PointSymbolizer>";

        String xmlRotate =
            "<PointSymbolizer version=\"1.1.0\"" +
                "                  xmlns=\"http://www.opengis.net/se\"" +
                "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                "<Geometry>\n" +
                "    <ogc:PropertyName>\npoint\n</ogc:PropertyName>\n" +
                "</Geometry>" +
                "<Graphic>" +
                "<ExternalGraphic>" +
                "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/bus.png\"/>" +
                "<Format>image/png</Format>" +
                "</ExternalGraphic>" +
                "<Rotation>90.0</Rotation>" +
                "</Graphic>" +
                "</PointSymbolizer>";

        String xmlSVGGraphicRotate = "<PointSymbolizer version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                    "<Geometry>\n" +
                    "    <ogc:PropertyName>\npoint\n</ogc:PropertyName>\n" +
                    "</Geometry>" +
                    "<Graphic>" +
                    "<ExternalGraphic>" +
                    "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/information.svg\"/>" +
                    "<Format>image/svg+xml</Format>" +
                    "</ExternalGraphic>" +
                    "<Size>40</Size>" +
                    "<Rotation>90.0</Rotation>" +
                    "</Graphic>" +
                    "</PointSymbolizer>";

        String xmlOpacity =
                "<PointSymbolizer version=\"1.1.0\"" +
                                   "                  xmlns=\"http://www.opengis.net/se\"" +
                                   "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                                   "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                                   "<Geometry>\n" +
                                   "    <ogc:PropertyName>\npoint\n</ogc:PropertyName>\n" +
                                   "</Geometry>" +
                                   "<Graphic>" +
                                   "<ExternalGraphic>" +
                                   "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/bus.png\"/>" +
                                   "<Format>image/png</Format>" +
                                   "</ExternalGraphic>" +
                                   "<Opacity>0.5</Opacity>" +
                                   "</Graphic>" +
                                   "</PointSymbolizer>";




}
