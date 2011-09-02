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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.geolatte.maprenderer.geotools.GTSpatialReference;
import org.geolatte.maprenderer.java2D.JAIMapGraphics;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.SpatialReference;
import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.geolatte.maprenderer.shape.BasicScalableStroke;
import org.geolatte.maprenderer.shape.ScalableStroke;
import org.geolatte.maprenderer.shape.ShapeAdapter;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jun 2, 2010
 */
public class TestOffsetRendering {

    /*TODO -- add tests for these cases:
        1. linestring with very small linesegments (relative to offset)
        2. Perpendicular offset == 0
        3. non-contigous paths (i.e. with intermediate moveTo's
     */


    private static final float LINE_WIDTH = 5.0f;
    private static final float OFFSET  = 8.0f;
    private static final float OFFSET_LINE_WIDTH = 1.0f;
    private static final int NUM_IMG = 90;

    private SpatialReference spatialReference;
    private SpatialExtent extent;
    private java.awt.Dimension dim = new java.awt.Dimension(512, 512);
    private ScalableStroke stroke;
    private ScalableStroke offsetStroke;
    private ScalableStroke negOffsetStroke;
    private GeometryFactory geomFactory;

    @Before
    public void setUp() throws SpatialReferenceCreationException {
        this.spatialReference = new GTSpatialReference("4236", true);
        this.extent = new SpatialExtent(-100, -100, 100, 100, spatialReference);
        this.stroke = new BasicScalableStroke(LINE_WIDTH); //e, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_BUTT);
        this.offsetStroke = new BasicScalableStroke(OFFSET_LINE_WIDTH); //, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_BUTT);
        this.offsetStroke.setPerpendicularOffset(OFFSET);
        this.negOffsetStroke = new BasicScalableStroke(OFFSET_LINE_WIDTH); //, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_BUTT);
        this.negOffsetStroke.setPerpendicularOffset(-OFFSET);


        geomFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4236);

    }


    @Test
    public void test_paint_lines_left_to_right_with_offset() throws IOException {
        renderImage(offsetStroke, "/tmp/img/offset-left-to-right-painter-", true);
    }

    @Test
    public void test_paint_lines_right_to_left_with_offset() throws IOException {
        renderImage(offsetStroke, "/tmp/img/offset-right-to-left-painter-", false);
    }


    @Test
    public void test_paint_lines_left_to_right_with_negative_offset() throws IOException {
        renderImage(negOffsetStroke, "/tmp/img/negative-offset-left-to-right-", true);
    }

    @Test
    public void test_paint_lines_right_to_left_with_negative_offset() throws IOException {
        renderImage(negOffsetStroke, "/tmp/img/negative-offset-right-to-left-", false);
    }

    private void renderImage(ScalableStroke offsetStroke, String path, boolean leftToRight) throws IOException {

        double theta = 2 * Math.PI / NUM_IMG;
        for (int i = 0; i < NUM_IMG; i++) {
            System.out.println("i = " + i);
            MapGraphics mapGraphics = new JAIMapGraphics(dim, spatialReference);
            mapGraphics.setToExtent(extent);

            LineString line = generateLineStrings(i, theta, leftToRight);
            mapGraphics.setStroke(stroke);
            mapGraphics.setColor(Color.BLACK);
            drawLineString(line, mapGraphics);

            mapGraphics.setStroke(offsetStroke);
            Color red = new Color(255,0,0,120);
//            mapGraphics.setColor(Color.RED);
            mapGraphics.setColor(red);
            drawLineString(line, mapGraphics);

            RenderedImage img = mapGraphics.createRendering();
            File file = new File(path + i + ".png");
            ImageIO.write(img, "PNG", file);

        }

    }


    private void drawLineString(LineString line, MapGraphics mapGraphics) {
        ShapeAdapter adapter = new ShapeAdapter(mapGraphics.getTransform());
        Shape[] shapes = adapter.toShape(line);
        for (Shape s : shapes) {
            mapGraphics.draw(s);
        }
    }

    private LineString generateLineStrings(int i, double theta, boolean leftToRight) {

        Coordinate[] coordinates = new Coordinate[3];
        if (leftToRight) {
            coordinates[0] = new Coordinate(-90, 0.0f);
            coordinates[1] = new Coordinate(0.0f, 0.0f);
            System.out.println("theta = " + i * theta);
            coordinates[2] = new Coordinate(90.0 * Math.cos(i * theta), 90.0 * Math.sin(i * theta));
        } else {
            coordinates[0] = new Coordinate(90, 0.0f);
            coordinates[1] = new Coordinate(0.0f, 0.0f);
            System.out.println("theta = " + i * theta);
            coordinates[2] = new Coordinate(-90.0 * Math.cos(i * theta), -90.0 * Math.sin(i * theta));
        }
        return geomFactory.createLineString(coordinates);
    }

}
