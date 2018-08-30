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

import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;

import java.awt.image.RenderedImage;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.geolatte.test.TestSupport.assertImageEquals;
import static org.geolatte.test.TestSupport.writeImageToDisk;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/16/11
 */
public class TestSVGTranscoder {

    public ExternalGraphicsRepository repo = new ExternalGraphicsRepository(
            new String[]{TestExternalGraphicsRepository.LOCAL_GRAPHICS_PACKAGE});

    final private static String SVG_SRC = "file://local.graphics/information.svg";
    final private static String PNG_SRC = "file://local.graphics/information.png";

    @Test
    public void testTranscodingImage() throws IOException {
        SVGDocument source = repo.getSVGFromCache(SVG_SRC);
        assertNotNull(source);

        SVGTranscoder transcoder = new SVGTranscoder();
        RenderedImage img = transcoder.transcode(source, 62, 65);
        assertNotNull(img);
        assertEquals(62, img.getWidth());
        assertEquals(65, img.getHeight());

        writeImageToDisk(img, "information.png", "PNG");
        assertImageEquals(repo.get(PNG_SRC), img);


    }

}
