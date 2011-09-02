/*
 * Copyright (c) 2011. Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
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
