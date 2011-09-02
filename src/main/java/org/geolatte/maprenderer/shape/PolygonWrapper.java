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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;


public class PolygonWrapper extends GeometryWrapper implements Shape {

    private final Polygon polygon;
    private final AffineTransform worldToImageTransform;

    PolygonWrapper(Polygon geometry, AffineTransform worldToImageTransform) {
        this.polygon = geometry;
        this.worldToImageTransform = worldToImageTransform;
    }


    public PathIterator getPathIterator(AffineTransform at) {
        return new PolygonPathIterator(this.polygon, at, worldToImageTransform);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new PolygonPathIterator(this.polygon, at, worldToImageTransform);
    }

    public Geometry getGeometry() {
        return this.polygon;
    }
}
