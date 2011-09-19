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

import java.awt.image.RenderedImage;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.geolatte.test.TestSupport.assertImageEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
        GraphicSource source = repo.get("file://local.graphics/bus.png");
        assertNotNull(source);

        assertTrue(source instanceof RenderedImageGraphicSource);

        RenderedImage expectedGraphic = ExpectedImages.getExpectedGraphic("bus.png");
        assertImageEquals(expectedGraphic, (RenderedImage)source.getGraphic());

    }

    @Test
    public void testSVGGraphicsReadFromClassPath() throws IOException {
        GraphicSource source = repo.get("file://local.graphics/information.svg");
        assertNotNull(source);
        assertTrue(source instanceof SVGDocumentGraphicSource);
        //TODO -- test for image equality (based on transcoding?).
    }

    @Test
    public void testGraphicsFromURL() throws IOException {
        GraphicSource source = repo.get(EXT_GRAPHIC_IMAGE_URL);
        assertNotNull(source);
        assertTrue(source instanceof RenderedImageGraphicSource);
        RenderedImage img1 = (RenderedImage)source.getGraphic();
        TestSupport.writeImageToDisk(img1, "logo3w.png", "PNG"); // for visual check

        //Test that the image is in the cache
        GraphicSource source2 = repo.getFromCache(EXT_GRAPHIC_IMAGE_URL);
        assertNotNull(source2);
        RenderedImage img2 = (RenderedImage) source2.getGraphic();
        assertImageEquals(img1, img2);
    }

    @Test
    public void testSVGGraphicsReadFromURL() throws IOException {
        GraphicSource source1 = repo.get(EXT_GRAPHIC_SVG_URL);
        assertNotNull(source1);
        assertTrue(source1 instanceof SVGDocumentGraphicSource);
        //TODO -- test for image equality (based on transcoding?).

        //Test that the image is in the cache
        GraphicSource source2 = repo.getFromCache(EXT_GRAPHIC_SVG_URL);
        assertNotNull(source2);
        assertEquals(source1.getGraphic(), source2.getGraphic());

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
            repo.storeInCache(null, new RenderedImageGraphicSource(expectedGraphic));
            fail();
        } catch (IllegalArgumentException e) {
            //OK
        }

        try {
            repo.storeInCache("key", null);
            fail();
        } catch (IllegalArgumentException e) {
            //OK
        }
    }


}
