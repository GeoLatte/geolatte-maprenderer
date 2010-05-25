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

package org.geolatte.test;

import com.vividsolutions.jts.geom.*;
import org.geolatte.core.reflection.Feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class MockPolygonFeature extends AbstractMockFeature {

    public MockPolygonFeature() {
        super();

    }

    protected Geometry generateGeom(){
        double startx = Math.random() * 90;
        double starty = Math.random() * 90;
        double width = Math.random() * 90;
        double height = Math.random() * 90;

        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(startx, starty),
                new Coordinate(startx, starty + height),
                new Coordinate(startx + width, starty + height),
                new Coordinate(startx + width, starty),
                new Coordinate(startx, starty),
        };
        LinearRing shell = geomFactory.createLinearRing(coordinates);
        return new Polygon(shell, null, geomFactory);        
    };

}