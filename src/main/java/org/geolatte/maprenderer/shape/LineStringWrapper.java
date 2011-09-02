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
import com.vividsolutions.jts.geom.LineString;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;


public class LineStringWrapper extends GeometryWrapper implements Shape {

    private final LineString lineString;
    private final AffineTransform worldToImageTransform;

    public LineStringWrapper(LineString lineString, AffineTransform worldToImageTransform) {
        this.lineString = lineString;
        this.worldToImageTransform = worldToImageTransform;
    }

    public Geometry getGeometry() {
        return this.lineString;
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return new LineStringPathIterator(this.lineString, at, worldToImageTransform);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new LineStringPathIterator(this.lineString, at, worldToImageTransform);
    }
}
