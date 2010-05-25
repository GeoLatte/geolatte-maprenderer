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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Jan 18, 2010
 * Time: 10:14:54 PM
 * To change this template use File | Settings | File Templates.
 */
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
