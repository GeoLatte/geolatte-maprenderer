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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import java.awt.geom.AffineTransform;

/**
 * Created by IntelliJ IDEA.
 * User: adnsgis
 * Date: Jan 29, 2010
 * Time: 11:43:56 AM
 * To change this template use File | Settings | File Templates.
 */
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
