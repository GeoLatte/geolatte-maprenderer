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

package org.geolatte.java2D;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geolatte.common.Feature;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.SingleCoordinateReferenceSystem;
import org.geolatte.maprenderer.java2D.AWTMapGraphics;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.test.MockLineStringFeature;
import org.geolatte.test.TestSupport;

import org.junit.Before;
import org.junit.Test;

import static org.geolatte.test.TestSupport.assertImageEquals;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class TestSimpleOffsetPainter {

    public static final SingleCoordinateReferenceSystem<C2D> CRS = CoordinateReferenceSystems.PROJECTED_2D_METER;

    private MapGraphics mapGraphics;
    private Envelope<C2D> extent;

    private List<Feature> features = new ArrayList<Feature>();

            
    @Before
    public void setUp() {
        this.extent = new Envelope<>(new C2D(5,5), new C2D(40,40), CRS);
        java.awt.Dimension dim = new java.awt.Dimension(512, 512);
        this.mapGraphics = new AWTMapGraphics( dim, extent);

        this.features.add( new MockLineStringFeature());

    }


    @Test
    public void test_paint_lines_with_offset() throws IOException {
        Painter painter = new SimpleOffsetPainter(this.mapGraphics);
        painter.paint(features);
        RenderedImage img = this.mapGraphics.createRendering();
        TestSupport.writeImageToDisk(img, "simple-offset-painter.png", "PNG");
        assertImageEquals("expected-simple-offset-painter.png", img);

    }




}
