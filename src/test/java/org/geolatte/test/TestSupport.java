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

import org.geolatte.maprenderer.java2D.ImageComparator;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.fail;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/10/11
 */
public class TestSupport {

    /**
     * The sub-directory of java.io.tmpdir where the
     * image files will be written during unit tests (if enabled).
     */
    final static public String TEMP_SUB_DIR = "img";

    /**
     * Java system property that controls whether to write image files to
     * TEST_WRITE_DIR during unit testing.
     *
     */
    final static public String WRITE_TEST_IMAGES_TO_DISK = "WRITE_TEST_FILES";

    /**
     * Determines whether image files are to be written to disk.
     * @return
     */
    public static boolean writeTestImagesToDiskIsActive(){
        if (System.getProperty(WRITE_TEST_IMAGES_TO_DISK) == null) return false;
        return true;
    }

    /**
     * Writes the image to disk using the specified name and format
     * @param img image to write
     * @param imageName name to use (path will be TEST_WRITE_DIR
     * @param type
     * @throws IOException
     */
    public static void writeImageToDisk(RenderedImage img, String imageName, String type) throws IOException {
        if (TestSupport.writeTestImagesToDiskIsActive()){
            File file = new File(getTempDir(),imageName);
            ImageIO.write(img, type, file);
        }
    }

    public static void assertImageEquals(RenderedImage img1, RenderedImage img2) {
        ImageComparator comparator = new ImageComparator();
        if (!comparator.equals(img1, img2)) {
            fail("Images not equal");
        }
    }

    public static void assertImageEquals(String expectedImageName, RenderedImage received) throws IOException {
        RenderedImage expected = ExpectedImages.getExpectedRenderedImage(expectedImageName);
        assertImageEquals(expected, received);
    }

    public static File getTempDir(){
        File tmp = new File(System.getProperty("java.io.tmpdir"));
        File imgDir = new File(tmp, TEMP_SUB_DIR);
        if (!imgDir.exists()) {
            imgDir.mkdir();
        }
        return imgDir;
    }

}
