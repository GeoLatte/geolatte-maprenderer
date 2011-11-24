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
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class MockPolygonFeature extends AbstractMockFeature {

    public MockPolygonFeature() {
        super();
    }

    public MockPolygonFeature(Geometry geom) {
        super(geom);
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

    public static MockPolygonFeature createRect(double minX, double minY, double maxX, double maxY) {
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(minX, minY),
                new Coordinate(minX, maxY),
                new Coordinate(maxX, maxY),
                new Coordinate(maxX, minY),
                new Coordinate(minX, minY)
    };
        LinearRing shell = geomFactory.createLinearRing(coordinates);
        return new MockPolygonFeature(geomFactory.createPolygon(shell, null));
    }

    @Override
    public String getGeometryName() {
        return "geometry";
    }
}