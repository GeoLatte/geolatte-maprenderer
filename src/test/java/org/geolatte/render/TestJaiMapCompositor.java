/*
 * This file is part of the GeoLatte project. This code is licenced under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * Copyright (C) 2010 - 2010 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee (http://www.Qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.render;

import org.geolatte.maprenderer.geotools.GTSpatialReferenceFactory;
import org.geolatte.maprenderer.java2D.JAIMapGraphics;
import org.geolatte.maprenderer.java2D.JaiMapCompositor;
import org.geolatte.maprenderer.map.MapCompositor;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.SpatialReferenceException;
import org.geolatte.maprenderer.reference.SpatialReferenceFactory;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.reference.SpatialReference;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Feb 15, 2010
 * Time: 3:45:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestJaiMapCompositor {

    final static public SpatialReferenceFactory crsFactory = new GTSpatialReferenceFactory();

    static public SpatialReference crs;

    static {
        try {
            crs = crsFactory.createSpatialReference("4326", true);
        } catch (SpatialReferenceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testOverlay() {


        List<RenderedImage> images = new ArrayList<RenderedImage>();

        MapGraphics map = new JAIMapGraphics(new Dimension(256, 256), crs);
        map.setToExtent(new SpatialExtent(0, 0, 90, 90, crs));
        map.setColor(new Color(0f, 1f, 0f, 0.5f));
        map.fillRect(10, 10, 50, 50);
        RenderedImage img = map.createRendering();
        writeToFile(img, "in1");
        images.add(img);


        map = new JAIMapGraphics(new Dimension(256, 256), crs);
        map.setToExtent(new SpatialExtent(0, 0, 90, 90, crs));
        map.setColor(new Color(0f, 0f, 1f, 0.5f));
        map.fillRect(10, 10, 10, 10);
        map.fillRect(20, 20, 10, 10);
        map.setColor(new Color(0f, 0f, 1f, 1.0f));
        map.fillRect(30, 30, 10, 10);
        map.setColor(new Color(0f, 0f, 1f, 0.8f));
        map.fillRect(70, 70, 10, 10);
        img = map.createRendering();
        writeToFile(img, "in2");
        images.add(img);

        map = new JAIMapGraphics(new Dimension(256, 256), crs);
        map.setToExtent(new SpatialExtent(0, 0, 90, 90, crs));
        map.setColor(Color.RED);

        map.drawOval(15, 15, 1, 1);
        map.drawOval(25, 25, 1, 1);
        map.drawOval(35, 35, 1, 1);
        map.drawOval(45, 45, 1, 1);
        img = map.createRendering();
        writeToFile(img, "in3");
        images.add(img);

        MapCompositor overlay = new JaiMapCompositor();
        RenderedImage result = overlay.overlay(images);

        writeToFile(result, "added");

    }

    private void writeToFile(RenderedImage img, String fn) {
        File f1 = new File("/tmp/img/" + fn + ".png");
        try {
            ImageIO.write(img, "PNG", f1);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
