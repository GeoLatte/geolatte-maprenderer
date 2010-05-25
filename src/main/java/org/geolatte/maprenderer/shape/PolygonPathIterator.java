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
import com.vividsolutions.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.AffineTransform;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Jan 18, 2010
 * Time: 10:45:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class PolygonPathIterator extends GeometryPathIterator {

    private static Logger LOGGER = LoggerFactory.getLogger(PolygonPathIterator.class);

    private final Polygon polygon;
    private int coordinateIndex = 0;
    private int ringIndex = -1;
    private LineString currentRing = null;
    private Coordinate currentCoordinate;


    public PolygonPathIterator(Polygon geom, AffineTransform transform, AffineTransform worldToImageTransform) {
        super(worldToImageTransform, transform);
        this.polygon = geom;
        this.currentRing = this.polygon.getExteriorRing();
        this.coordinateIndex = 0;
        currentCoordinate = this.currentRing.getCoordinateN(this.coordinateIndex);
    }

    protected void advance() {
        coordinateIndex++;
        if (beyondCurrentRing())
            nextRing();
        setCurrentCoordinate(currentRing.getCoordinateN(coordinateIndex));
    }

    protected void setCurrentCoordinate(Coordinate coordinate) {
        this.currentCoordinate = coordinate;
    }

    protected Coordinate getCurrentCoordinate() {
        return this.currentCoordinate;
    }

    @Override
    protected int determineOperation() {
        if (coordinateIndex == 0) {
            return SEG_MOVETO;
        } else if (coordinateIndex == currentRing.getNumPoints() - 1) {
            return SEG_CLOSE;
        } else {
            return SEG_LINETO;
        }
    }

    private void nextRing() {
        coordinateIndex = 0;
        ringIndex++;
        if (ringIndex < this.polygon.getNumInteriorRing()) {
            currentRing = polygon.getInteriorRingN(ringIndex);
        } else {
            setIsDone();
        }
    }

    private boolean beyondCurrentRing() {
        return (coordinateIndex >= currentRing.getNumPoints());
    }


}
