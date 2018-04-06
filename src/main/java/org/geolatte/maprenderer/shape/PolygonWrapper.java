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

package org.geolatte.maprenderer.shape;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Polygon;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;


public class PolygonWrapper extends GeometryWrapper implements Shape {

    private final Polygon<C2D> polygon;
    private final AffineTransform worldToImageTransform;

    PolygonWrapper(Polygon<C2D> geometry, AffineTransform worldToImageTransform) {
        this.polygon = geometry;
        this.worldToImageTransform = worldToImageTransform;
    }


    public PathIterator getPathIterator(AffineTransform at) {
        return new PolygonPathIterator(this.polygon, at, worldToImageTransform);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new PolygonPathIterator(this.polygon, at, worldToImageTransform);
    }

    public Polygon<C2D> getGeometry() {
        return this.polygon;
    }
}
