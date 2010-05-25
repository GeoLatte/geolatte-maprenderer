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
import java.awt.geom.AffineTransform;


/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Jan 18, 2010
 * Time: 10:14:15 PM
 * To change this template use File | Settings | File Templates.
 */
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
