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

package org.geolatte.java2D;

import org.geolatte.core.Feature;
import org.geolatte.maprenderer.geotools.GTSpatialReference;
import org.geolatte.maprenderer.java2D.JAIMapGraphics;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.SpatialReference;
import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.geolatte.test.MockLineStringFeature;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class TestBasicPainter {


    private MapGraphics mapGraphics;
    private SpatialReference spatialReference;
    private SpatialExtent extent;

    private List<Feature> features = new ArrayList<Feature>();

            
    @Before
    public void setUp() throws SpatialReferenceCreationException {
        this.spatialReference = new GTSpatialReference("4236", true);
        this.extent = new SpatialExtent(5,5,40,40, spatialReference);
        java.awt.Dimension dim = new java.awt.Dimension(512, 512);
        this.mapGraphics = new JAIMapGraphics(dim, spatialReference);

        this.features.add( new MockLineStringFeature());

    }


    @Test
    public void test_paint_lines_with_offset() throws IOException {
        Painter painter = new SimpleOffsetPainter();
        this.mapGraphics.setToExtent(extent);
        painter.paint(this.mapGraphics, features);
        RenderedImage img = this.mapGraphics.createRendering();
        File file = new File("/tmp/img/test-simple-offset-painter.png");
        ImageIO.write(img, "PNG", file);

    }


}
