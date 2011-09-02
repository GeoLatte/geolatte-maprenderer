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
import java.awt.geom.AffineTransform;

public class ShapeAdapter {

    final private AffineTransform worldToImageTransform;

    public ShapeAdapter(AffineTransform worldToImageTransform) {
        this.worldToImageTransform = worldToImageTransform;
    }

    public Shape[] toShape(Geometry geometry) {
        if (geometry == null) return new Shape[]{};
        if (geometry instanceof Polygon) {
            return new Shape[]{new PolygonWrapper((Polygon) geometry, worldToImageTransform)};
        } else if (geometry instanceof MultiPolygon) {
            MultiPolygon multiPolygon = (MultiPolygon) geometry;
            Shape[] shapes = new Shape[multiPolygon.getNumGeometries()];
            load(shapes, multiPolygon);
            return shapes;
        } else if (geometry instanceof LineString) {
            return new Shape[]{new LineStringWrapper((LineString) geometry, worldToImageTransform)};
        } else if (geometry instanceof MultiLineString) {
            MultiLineString multiLineString = (MultiLineString) geometry;
            Shape[] shapes = new Shape[multiLineString.getNumGeometries()];
            loadShape(shapes, multiLineString);
            return shapes;
        }
        throw new UnsupportedOperationException("Can't adapt shapes fo type " + geometry.getGeometryType());
    }

    private void loadShape(Shape[] shapes, MultiLineString multiLineString) {
        for (int i = 0; i < shapes.length; i++) {
            LineString ls = (LineString) multiLineString.getGeometryN(i);
            shapes[i] = new LineStringWrapper(ls, worldToImageTransform);
        }
    }

    private void load(Shape[] shapes, MultiPolygon multiPolygon) {
        for (int i = 0; i < shapes.length; i++) {
            Polygon pg = (Polygon) multiPolygon.getGeometryN(i);
            shapes[i] = new PolygonWrapper(pg, worldToImageTransform);
        }
    }

    ;


}
