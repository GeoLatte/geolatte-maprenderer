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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.SingleCoordinateReferenceSystem;
import org.geolatte.maprenderer.java2D.JAIMapGraphics;
import org.geolatte.maprenderer.java2D.PerpendicularOffsetStroke;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.shape.ShapeAdapter;
import org.geolatte.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.IOException;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jun 2, 2010
 */
public class TestOffsetRendering {

    public static final SingleCoordinateReferenceSystem<C2D> CRS = CoordinateReferenceSystems.PROJECTED_2D_METER;
    /*TODO -- add tests for these cases:
        1. linestring with very small linesegments (relative to offset)
        2. non-contiguous paths (i.e. with intermediate moveTo's
     */


    private static final float LINE_WIDTH = 5.0f;
    private static final float OFFSET = 8.0f;
    private static final float NEG_OFFSET = -8.0f;
    private static final float OFFSET_LINE_WIDTH = 2.0f;
    private static final int NUM_IMG = 250;

    private Envelope<C2D> extent;
    private java.awt.Dimension dim = new java.awt.Dimension(512, 512);
    private PerpendicularOffsetStroke stroke;
    private PerpendicularOffsetStroke offsetStroke;
    private PerpendicularOffsetStroke negOffsetStroke;
    private GeometryFactory geomFactory;

    @Before
    public void setUp() {
        this.extent = new Envelope<>(new C2D(-100, -100), new C2D(100, 100), CRS);
        this.stroke = new PerpendicularOffsetStroke(LINE_WIDTH); //e, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_BUTT);
        this.offsetStroke = new PerpendicularOffsetStroke(OFFSET_LINE_WIDTH, OFFSET); //, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_BUTT);
        this.negOffsetStroke = new PerpendicularOffsetStroke(OFFSET_LINE_WIDTH, NEG_OFFSET); //, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_BUTT);
        geomFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4236);

    }


    @Test
    public void test_paint_lines_left_to_right_with_offset() throws IOException {
        renderImage(offsetStroke, "offset-left-to-right-painter-", true);
    }

    @Test
    public void test_paint_lines_right_to_left_with_offset() throws IOException {
        renderImage(offsetStroke, "offset-right-to-left-painter-", false);
    }


    @Test
    public void test_paint_lines_left_to_right_with_negative_offset() throws IOException {
        renderImage(negOffsetStroke, "negative-offset-left-to-right-", true);
    }

    @Test
    public void test_paint_lines_right_to_left_with_negative_offset() throws IOException {
        renderImage(negOffsetStroke, "negative-offset-right-to-left-", false);
    }

    @Test
    public void test_paint_close_by_corner_cases() throws IOException {
        double[] angles = new double[]{
                Math.PI - 3 * PerpendicularOffsetStroke.EPSILON,
                Math.PI + 3 * PerpendicularOffsetStroke.EPSILON,
                -Math.PI - 3 * PerpendicularOffsetStroke.EPSILON,
                - Math.PI + 3 * PerpendicularOffsetStroke.EPSILON
        };
        renderImage(negOffsetStroke, "cornercase-negative-offset-right-to-left-", false, angles);
        renderImage(negOffsetStroke, "cornercase-negative-offset-left-to-right-", true, angles);
        renderImage(offsetStroke, "cornercase-offset-right-to-left-", false, angles);
        renderImage(offsetStroke, "cornercase-offset-left-to-right-", true, angles);

    }

    private void renderImage(PerpendicularOffsetStroke offsetStroke, String path, boolean leftToRight) throws IOException {
        double[] angles = new double[NUM_IMG];
        double theta = 2 * Math.PI / NUM_IMG;
        for (int i = 0; i < NUM_IMG; i++) {
            angles[i] = i * theta;
        }
        renderImage(offsetStroke, path, leftToRight, angles);
    }

    private void renderImage(PerpendicularOffsetStroke offsetStroke, String path, boolean leftToRight, double[] angles) throws IOException {

        for (int i = 0; i < angles.length; i++) {
            System.out.println("i = " + i);
            MapGraphics mapGraphics = new JAIMapGraphics(dim, extent);

            LineString line = generateLineStrings(angles[i], leftToRight);
            mapGraphics.setStroke(stroke);
            mapGraphics.setColor(Color.BLACK);
            drawLineString(line, mapGraphics);

            mapGraphics.setStroke(offsetStroke);
            Color red = new Color(255, 0, 0, 120);
            mapGraphics.setColor(red);
            drawLineString(line, mapGraphics);

            RenderedImage img = mapGraphics.createRendering();
            TestSupport.writeImageToDisk(img, path + i + ".png", "PNG");
        }
    }


    private void drawLineString(LineString line, MapGraphics mapGraphics) {
        ShapeAdapter adapter = new ShapeAdapter(mapGraphics.getTransform());
        Shape[] shapes = adapter.toShape(line);
        for (Shape s : shapes) {
            mapGraphics.draw(s);
        }
    }

    private LineString generateLineStrings(double theta, boolean leftToRight) {

        Coordinate[] coordinates = new Coordinate[3];
        if (leftToRight) {
            coordinates[0] = new Coordinate(-90, 0.0f);
            coordinates[1] = new Coordinate(0.0f, 0.0f);
            System.out.println("theta = " + theta);
            coordinates[2] = new Coordinate(90.0 * Math.cos(theta), 90.0 * Math.sin(theta));
        } else {
            coordinates[0] = new Coordinate(90, 0.0f);
            coordinates[1] = new Coordinate(0.0f, 0.0f);
            System.out.println("theta = " + theta);
            coordinates[2] = new Coordinate(-90.0 * Math.cos(theta), -90.0 * Math.sin(theta));
        }
        return geomFactory.createLineString(coordinates);
    }

}
