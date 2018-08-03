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

import net.opengis.se.v_1_1_0.ExternalGraphicType;
import org.geolatte.maprenderer.sld.SLD;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/12/11
 */
public class TestExternalGraphic {

    ExternalGraphic simple;

    @Before
    public void before() {
        ExternalGraphicType type = SLD.instance().read(xmlValidOnlineExternalGraphic, ExternalGraphicType.class);
        simple = new ExternalGraphic(type);
    }

    @Test
    public void testOnlineResource() {
        assertEquals("http://www.geolatte.org/test.png", simple.getUrl());
    }

    @Test
    public void testFormat() {
        assertEquals("image/png", simple.getFormat());
    }

    @Test
    public void testColorReplacementThrowsIllegalArgumentException() {
        ExternalGraphicType type = SLD.instance().read(xmlOnlineExternalGraphicWithColorReplacement, ExternalGraphicType.class);
        try {
            new ExternalGraphic(type);
            fail();
        } catch (IllegalArgumentException e) {
            //OK
        }
    }

    @Test
    public void testInlineContentThrowsIllegalArgumentException() {
        ExternalGraphicType type = SLD.instance().read(xmlInlineExternalGraphic, ExternalGraphicType.class);
        try {
            new ExternalGraphic(type);
            fail();
        } catch (IllegalArgumentException e) {
            //OK
        }
    }


    private static String xmlValidOnlineExternalGraphic =
            "<ExternalGraphic version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +

                    "<OnlineResource xlink:type=\"simple\" xlink:href=\"http://www.geolatte.org/test.png\"/>" +
                    "<Format>image/png</Format>" +
                    "</ExternalGraphic>";

    private static String xmlOnlineExternalGraphicWithColorReplacement =
            "<ExternalGraphic version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\" " +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +

                    "<OnlineResource xlink:type=\"simple\" xlink:href=\"http://www.geolatte.org/test.png\"/>" +
                    "<Format>image/png</Format>" +
                    "<ColorReplacement>" +
                    "<Recode>" +
                    "<LookupValue>ExternalGraphic</LookupValue>" +
                    "<MapItem>" +
                    "<Data>#0000FF</Data>" +
                    "<Value>#FF0000</Value>" +
                    "</MapItem>" +
                    "</Recode>" +
                    "</ColorReplacement>" +
                    "</ExternalGraphic>";

    private static String xmlInlineExternalGraphic =
            "<ExternalGraphic version=\"1.1.0\"" +
                    "                  xmlns=\"http://www.opengis.net/se\"" +
                    "                  xmlns:ogc=\"http://www.opengis.net/ogc\"" +
                    "                  xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
                    "<InlineContent>1 2 3 .....</InlineContent>" +
                    "</ExternalGraphic>";

}

