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
