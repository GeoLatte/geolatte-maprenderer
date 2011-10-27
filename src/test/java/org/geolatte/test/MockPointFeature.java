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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/23/11
 */
public class MockPointFeature extends AbstractMockFeature {

    public MockPointFeature(Point pnt) {
        super(pnt);
    }

    public MockPointFeature(){
        super();
    }

    @Override
    protected Geometry generateGeom() {
        double x = Math.random() * 90;
        double y = Math.random() * 90;
        return geomFactory.createPoint(new Coordinate(x, y));
    }

    public static MockPointFeature createPoint(double x, double y){
        Point pnt = geomFactory.createPoint(new Coordinate(x, y));
        return new MockPointFeature(pnt);
    }
}
