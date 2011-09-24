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

import net.opengis.se.v_1_1_0.GraphicType;
import org.geolatte.maprenderer.sld.SLD;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;

import static org.junit.Assert.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/16/11
 */
public class TestGraphic {

    private Graphic graphic;
    private Graphic defaultGraphic;
    private ExternalGraphicsRepository egRepo;

    @Before
    public void before() {
        ExternalGraphicsRepository egRepo = new ExternalGraphicsRepository(new String[]{
                TestExternalGraphicsRepository.LOCAL_GRAPHICS_PACKAGE
        });

        GraphicType graphicGT = SLD.instance().read(xmlFullyDefinedGraphic, GraphicType.class);
        graphic = new Graphic(graphicGT);
        GraphicType simpleGT = SLD.instance().read(xmlSimpleGraphic, GraphicType.class);
        defaultGraphic = new Graphic(simpleGT);
    }

    @Test
    public void testGraphicHasExternalGraphic() {
        assertEquals(1, graphic.getSources().size());
        MarkOrExternalGraphicHolder holder = graphic.getSources().get(0);
        assertTrue(holder.isExternalGraphic());
        assertFalse(holder.isMark());
        assertEquals("file://local.graphics/bus.png", holder.getExternalGrapic().getUrl());
        assertEquals("image/png", holder.getExternalGrapic().getFormat());
    }

    @Test
    public void testDefaultSourceIsExternalGraphicAvailable() {
        assertEquals(1, defaultGraphic.getSources().size());
        MarkOrExternalGraphicHolder holder = defaultGraphic.getSources().get(0);
        assertTrue(holder.isMark());
        assertFalse(holder.isExternalGraphic());
        assertTrue(holder.getMark().getWellKnownName().equalsIgnoreCase("square"));
        //TODO -- test other default properties (color, opacity, default size)
    }

    @Test
    public void testOpacity(){
        assertEquals(0.9f, graphic.getOpacity(), 0.000001f);
    }

    @Test
    public void testDefaultOpacity(){
        assertEquals(1.0f, defaultGraphic.getOpacity(), 0.000001f);
    }

    @Test
    public void testSize(){
        assertEquals(10f, graphic.getSize(), 0.000001f);
    }

    @Test
    public void testDefaultSize(){
        assertEquals(16f, defaultGraphic.getSize(), 0.000001f);
    }

    @Test
    public void testRotation() {
        assertEquals(45f, graphic.getRotation(), 0.00001f);
    }

    @Test
    public void testDefaultRotation() {
        assertEquals(0f, defaultGraphic.getRotation(), 0.00001f);
    }

    @Test
    public void testDisplacement(){
        assertEquals(new Point2D.Float(15, 25), graphic.getDisplacement());
    }

    @Test
    public void testDefaultDisplacement() {
        assertEquals(new Point2D.Float(0f, 0f), defaultGraphic.getDisplacement());
    }

    @Test
    public void testAnchorPoint() {
        assertEquals(new Point2D.Float(0.75f, 0.65f), graphic.getAnchorPoint());
    }

    @Test
    public void testDefaultAnchorPoint() {
        assertEquals(new Point2D.Float(0.5f, 0.5f), defaultGraphic.getAnchorPoint());
    }

    @Test
    public void testSizeIsSet(){
        assertTrue(graphic.isSizeSet());
    }

    @Test
    public void testSizeDefaultNotSet(){
        assertFalse(defaultGraphic.isSizeSet());
    }

    private static String xmlFullyDefinedGraphic =
            "<Graphic version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
                    "<ExternalGraphic>" +
                        "<OnlineResource xlink:type=\"simple\" xlink:href=\"file://local.graphics/bus.png\"/>" +
                        "<Format>image/png</Format>" +
                    "</ExternalGraphic>" +
                    "<Opacity>0.9</Opacity>" +
                    "<Size>10</Size>" +
                    "<Rotation>45</Rotation>" +
                    "<AnchorPoint>" +
                        "<AnchorPointX>0.75</AnchorPointX>" +
                        "<AnchorPointY>0.65</AnchorPointY>" +
                    "</AnchorPoint>" +
                    "<Displacement>" +
                        "<DisplacementX>15</DisplacementX>" +
                        "<DisplacementY>25</DisplacementY>" +
                    "</Displacement>" +
                    "<Size>10</Size>" +
            "</Graphic>";

    //To test the defaults
    private static String xmlSimpleGraphic =
            "<Graphic version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
            "</Graphic>";
}
