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

package org.geolatte.render;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.SingleCoordinateReferenceSystem;
import org.geolatte.maprenderer.java2D.AWTMapGraphics;
import org.geolatte.maprenderer.java2D.AWTMapCompositor;
import org.geolatte.maprenderer.map.MapCompositor;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.test.TestSupport;
import org.junit.Test;

import java.awt.*;
import java.awt.image.RenderedImage;
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
public class TestAWTMapCompositor {

    public static final SingleCoordinateReferenceSystem<C2D> CRS = CoordinateReferenceSystems.PROJECTED_2D_METER;

    @Test
    public void testOverlay() throws IOException {


        List<RenderedImage> images = new ArrayList<RenderedImage>();

        MapGraphics map = new AWTMapGraphics( new Dimension( 256, 256),
                                              new Envelope<C2D>( new C2D(0, 0), new C2D(90, 90), CRS));
        map.setColor(new Color(0f, 1f, 0f, 0.5f));
        map.fillRect(10, 10, 50, 50);
        RenderedImage img = map.createRendering();
        writeToFile(img, "in1");
        images.add(img);


        map = new AWTMapGraphics( new Dimension( 256, 256),
                                  new Envelope<C2D>(new C2D(0, 0), new C2D( 90, 90), CRS));
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

        map = new AWTMapGraphics( new Dimension( 256, 256),
                                  new Envelope<C2D>(new C2D(0, 0), new C2D( 90, 90), CRS));
        map.setColor(Color.RED);

        map.drawOval(15, 15, 1, 1);
        map.drawOval(25, 25, 1, 1);
        map.drawOval(35, 35, 1, 1);
        map.drawOval(45, 45, 1, 1);
        img = map.createRendering();
        writeToFile(img, "in3");
        images.add(img);

        MapCompositor overlay = new AWTMapCompositor();
        RenderedImage result = overlay.overlay(images);

        writeToFile(result, "added");

    }

    private void writeToFile(RenderedImage img, String fn) throws IOException {
        TestSupport.writeImageToDisk(img, fn, "PNG");
    }

}
