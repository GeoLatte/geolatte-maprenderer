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

package org.geolatte.test;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * A support class that retrieves the test images from the file system.
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/10/11
 */
public class ExpectedImages {

    final public static String PACKAGE_EXPECTED = "img/expected/";
    final public static String PACKAGE_GRAPHICS = "graphics/";

    public static RenderedImage getExpectedRenderedImage(String str) throws IOException {
        return readImage(PACKAGE_EXPECTED + str);
    }

    public static RenderedImage getExpectedGraphic(String str) throws IOException {
        return readImage(PACKAGE_GRAPHICS + str);
    }

    private static RenderedImage readImage(String str) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(str);
        if (in == null) throw new IOException("File not found: " + str);
        try {
            return ImageIO.read(in);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                //nothing to do.
            }
        }
    }

}
