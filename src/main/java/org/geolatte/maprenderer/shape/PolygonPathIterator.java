/*
 * Copyright (c) 2011. Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.maprenderer.shape;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.AffineTransform;


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
