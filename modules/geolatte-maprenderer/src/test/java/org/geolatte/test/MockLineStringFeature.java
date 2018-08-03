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

package org.geolatte.test;

import org.geolatte.geom.*;
import org.geolatte.geom.crs.CrsId;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.linestring;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class MockLineStringFeature extends AbstractMockFeature {


    public MockLineStringFeature(Geometry geom) {
        super(geom);
    }



    public MockLineStringFeature() {
        super( linestring(CRS, c(10, 10), c(20, 22),
                          c(30, 10), c(40, 20)
                          ) );
    }

    public static MockLineStringFeature createLine(double x1, double y1, double x2, double y2) {
        return new MockLineStringFeature( linestring(CRS, c(x1,y1), c(x2,y2)));
    }
}
