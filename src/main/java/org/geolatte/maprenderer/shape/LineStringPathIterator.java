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


import java.awt.geom.AffineTransform;

import org.geolatte.geom.C2D;
import org.geolatte.geom.LineString;


public class LineStringPathIterator extends GeometryPathIterator {

    final private LineString<C2D> lineString;
    private C2D currentCoordinate;
    private int coordinateIndex = 0;


    public LineStringPathIterator(LineString<C2D> lineString, AffineTransform transform, AffineTransform imgToWorldTransform) {
        super(imgToWorldTransform, transform);
        this.lineString = lineString;
        this.currentCoordinate = lineString.getStartPosition();
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
        setCurrentCoordinate(this.lineString.getPositionN(coordinateIndex));
    }

    private boolean beyondCurrentLineString() {
        return coordinateIndex >= this.lineString.getNumPositions();
    }

    @Override
    void setCurrentCoordinate(C2D coordinate) {
        this.currentCoordinate = coordinate;
    }

    @Override
    C2D getCurrentCoordinate() {
        return this.currentCoordinate;
    }


    protected int determineOperation() {
        if (coordinateIndex == 0) {
            return SEG_MOVETO;
        } else return SEG_LINETO;
    }


}
