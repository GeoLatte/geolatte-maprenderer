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

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class MockLineStringFeature extends AbstractMockFeature{


    protected Geometry generateGeom() {
        double startx = 10;
        double starty = 10;

        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(startx, starty),
                new Coordinate(startx + 10.0, starty + 12.0),
                new Coordinate(startx + 20.0, starty),
                new Coordinate(startx + 30, starty+10.0)
        };
        return geomFactory.createLineString(coordinates);
    }


}
