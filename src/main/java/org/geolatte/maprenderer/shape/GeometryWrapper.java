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

package org.geolatte.maprenderer.shape;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: adnsgis
 * Date: Jan 29, 2010
 * Time: 11:37:29 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GeometryWrapper {

    public abstract Geometry getGeometry();


    public Rectangle getBounds() {
        Envelope env = getGeometry().getEnvelopeInternal();
        int minX = (int) Math.floor(env.getMinX());
        int minY = (int) Math.floor(env.getMinY());
        int width = (int) Math.ceil(env.getWidth());
        int height = (int) Math.ceil(env.getHeight());
        return new Rectangle(minX, minY, width, height);
    }


    public Rectangle2D getBounds2D() {
        Envelope env = getGeometry().getEnvelopeInternal();
        return new Rectangle2D.Double(env.getMinX(), env.getMinY(), env.getWidth(), env.getHeight());
    }

    public boolean contains(double x, double y) {
        Geometry geom = getGeometry();
        GeometryFactory factory = geom.getFactory();
        com.vividsolutions.jts.geom.Point pnt = factory.createPoint(new Coordinate(x, y));
        pnt.setSRID(geom.getSRID());
        return geom.contains(pnt);
    }

    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    public boolean intersects(double x, double y, double w, double h) {
        Polygon polygon = toPolygon(x, y, w, h);
        return getGeometry().intersects(polygon);
    }

    public boolean intersects(Rectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public boolean contains(double x, double y, double w, double h) {
        Polygon polygon = toPolygon(x, y, w, h);
        return getGeometry().contains(polygon);
    }

    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    private Polygon toPolygon(double x, double y, double w, double h) {
        GeometryFactory factory = getGeometry().getFactory();
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(x, y);
        coordinates[1] = new Coordinate(x + w, y);
        coordinates[2] = new Coordinate(x + w, y + h);
        coordinates[3] = new Coordinate(x, y + h);
        coordinates[4] = new Coordinate(x, y);
        LinearRing lr = factory.createLinearRing(coordinates);
        Polygon polygon = factory.createPolygon(lr, new LinearRing[]{});
        return polygon;
    }
}
