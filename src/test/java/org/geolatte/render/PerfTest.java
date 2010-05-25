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

import org.geolatte.core.reflection.Feature;
import org.geolatte.maprenderer.geotools.GTSpatialReference;
import org.geolatte.maprenderer.sld.SLDBasedPainter;
import org.geolatte.maprenderer.java2D.JAIMapGraphics;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.geolatte.maprenderer.reference.SpatialReference;
import org.geolatte.maprenderer.shape.BasicScalableStroke;
import org.geolatte.maprenderer.sld.FeatureTypeStyle;
import org.geolatte.maprenderer.sld.symbolizer.PolygonSymbolizer;
import org.geolatte.maprenderer.sld.symbolizer.Rule;
import org.geolatte.test.MockFeatures;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Feb 22, 2010
 * Time: 9:32:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class PerfTest {


    public static void main(String[] args) throws SpatialReferenceCreationException, IOException {

        Iterable<Feature> features = new MockFeatures(5000);
        SpatialReference spRef = new GTSpatialReference("4236", true);

        java.awt.Dimension dim = new java.awt.Dimension(256, 256);

//        ColorModel cm = new DirectColorModel(space, 8,48,12,3,0,true, DataBuffer.TYPE_BYTE);
        ColorSpace space = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new DirectColorModel(space, 32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000, true, DataBuffer.TYPE_INT);

        MapGraphics graphics = new JAIMapGraphics(dim, spRef, false);
        SpatialExtent extent = new SpatialExtent(0, 0, 90, 90, spRef);
        graphics.setToExtent(extent);
        FeatureTypeStyle fts = createFeatureTypeStyle();
        SLDBasedPainter painter = new SLDBasedPainter(fts);
        long t1 = System.currentTimeMillis();
        System.out.print("Start rendering....");
        painter.paint(graphics, features);
        RenderedImage img = graphics.createRendering();
        long t2 = System.currentTimeMillis();
        System.out.println("Rendering took: " + (t2 - t1) + " ms.");
        File file = new File("/tmp/img/test-1.png");
        ImageIO.write(img, "PNG", file);

    }

    private static FeatureTypeStyle createFeatureTypeStyle() {
        FeatureTypeStyle fts = new FeatureTypeStyle();
        Rule rule = new Rule();
        PolygonSymbolizer symbolizer = new PolygonSymbolizer();
        symbolizer.setStroke(new BasicScalableStroke(1f));
        symbolizer.setStrokeColor(Color.BLACK);
        symbolizer.setFillColor(Color.RED);
        rule.getSymbolizers().add(symbolizer);
        fts.getRules().add(rule);
        return fts;
    }


  

}


