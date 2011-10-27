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

import org.geolatte.test.ExpectedImages;
import org.geolatte.test.TestSupport;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;

import static org.geolatte.test.TestSupport.assertImageEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/14/11
 */
public class TestExternalGraphicsRepository {

    public static String LOCAL_GRAPHICS_PACKAGE = "graphics";

    //TODO can we link to a more stable graphics?
    private static String EXT_GRAPHIC_IMAGE_URL = "http://www.google.com/intl/en_com/images/srpr/logo3w.png";
    private static String EXT_GRAPHIC_SVG_URL = "http://www.gbwiki.net/wiki/images/3/35/Information_icon.svg";


    private ExternalGraphicsRepository repo;

    @Before
    public void before(){
         repo = new ExternalGraphicsRepository(new String[]{LOCAL_GRAPHICS_PACKAGE});
    }

    @Test
    public void testImageGraphicsReadFromClassPath() throws IOException {
        BufferedImage image = repo.get("file://local.graphics/bus.png");
        assertNotNull(image);
        RenderedImage expectedGraphic = ExpectedImages.getExpectedGraphic("bus.png");
        assertImageEquals(expectedGraphic, image);

    }

    @Test
    public void testSVGGraphicsReadFromClassPath() throws IOException {
        SVGDocument svg = repo.getSVGFromCache("file://local.graphics/information.svg");
        assertNotNull(svg);
    }

    @Test
    public void testGraphicsFromURL() throws IOException {
        BufferedImage image = repo.get(EXT_GRAPHIC_IMAGE_URL);
        assertNotNull(image);
        TestSupport.writeImageToDisk(image, "logo3w.png", "PNG"); // for visual check

        //Test that the image is in the cache
        BufferedImage fromCache = repo.getFromCache(new ExternalGraphicsRepository.ImageKey(EXT_GRAPHIC_IMAGE_URL));
        assertNotNull(fromCache);
        assertImageEquals(image, fromCache);
    }

    @Test
    public void testSVGGraphicsReadFromURL() throws IOException {
        BufferedImage img = repo.get(EXT_GRAPHIC_SVG_URL);
        assertNotNull(img);

        //Test that the image is in the cache
        SVGDocument svg = repo.getSVGFromCache(EXT_GRAPHIC_SVG_URL);
        assertNotNull(svg);
    }

    @Test
    public void test404Url() {
        try {
            repo.get("http://localhost/blabla.png");
            fail();
        } catch (IOException e) {
            //OK
        }

    }

    @Test
    public void testUrlToNonGraphic() {
        try {
            repo.get("http://www.geolatte.org/");
            fail();
        } catch (IOException e) {
            //OK
        }

    }

    @Test
    public void testStoringNullValuesInRepoFails() throws IOException {
        RenderedImage expectedGraphic = ExpectedImages.getExpectedGraphic("bus.png");
        try {
            repo.storeInCache(null, (BufferedImage)expectedGraphic);
            fail();
        } catch (IllegalArgumentException e) {
            //OK
        }

        try {
            repo.storeInCache(new ExternalGraphicsRepository.ImageKey("key"), null);
            fail();
        } catch (IllegalArgumentException e) {
            //OK
        }
    }


}
