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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import java.awt.geom.AffineTransform;


public class LineStringPathIterator extends GeometryPathIterator {

    final private LineString lineString;
    private Coordinate currentCoordinate;
    private int coordinateIndex = 0;


    public LineStringPathIterator(LineString lineString, AffineTransform transform, AffineTransform imgToWorldTransform) {
        super(imgToWorldTransform, transform);
        this.lineString = lineString;
        this.currentCoordinate = lineString.getCoordinateN(0);
    }

    @Override
    public int getWindingRule() {
        return WIND_EVEN_ODD;
    }

    @Override
    protected void advance() {
        coordinateIndex++;
        if (beyondCurrentLineString()) {
            setIsDone();
            return;
        }
        setCurrentCoordinate(this.lineString.getCoordinateN(coordinateIndex));
    }

    private boolean beyondCurrentLineString() {
        return coordinateIndex >= this.lineString.getNumPoints();
    }

    @Override
    void setCurrentCoordinate(Coordinate coordinate) {
        this.currentCoordinate = coordinate;
    }

    @Override
    Coordinate getCurrentCoordinate() {
        return this.currentCoordinate;
    }


    protected int determineOperation() {
        if (coordinateIndex == 0) {
            return SEG_MOVETO;
        } else return SEG_LINETO;
    }


}
