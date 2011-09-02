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

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;


abstract public class GeometryPathIterator implements PathIterator {

    protected final AffineTransform transform;
    protected final AffineTransform worldToImageTransform;
    private boolean done = false;
    private double[] coordinateBuffer = new double[2];
    private double[] pixelCoordinateBuffer = new double[2];
    private Coordinate previousCoordinate = null;
    private int[] previousPixelCoordinate = new int[2];

    public GeometryPathIterator(AffineTransform worldToImageTransform, AffineTransform transform) {
        this.worldToImageTransform = worldToImageTransform;
        this.transform = transform;
    }

    public int getWindingRule() {
        return WIND_EVEN_ODD;
    }

    public boolean isDone() {
        return this.done;
    }

    public void next() {
        do {
            advance();
        } while (!pixelDifferentFromPrevious() && !isDone());
    }


    abstract protected void advance();

    abstract void setCurrentCoordinate(Coordinate coordinate);

    abstract Coordinate getCurrentCoordinate();

    protected void setIsDone() {
        this.done = true;
    }

    /**
     * Determines whether this pixel is actually different from the previous pixel
     *
     * @return
     */
    private boolean pixelDifferentFromPrevious() {
        if (previousCoordinate == null) {
            return true;
        }
        coordinateBuffer[0] = getCurrentCoordinate().x;
        coordinateBuffer[1] = getCurrentCoordinate().y;
        worldToImageTransform.transform(coordinateBuffer, 0, pixelCoordinateBuffer, 0, 1);
        if (previousPixelCoordinate[0] != (int) pixelCoordinateBuffer[0] || previousPixelCoordinate[1] != (int) pixelCoordinateBuffer[1]) {
            previousPixelCoordinate[0] = (int) pixelCoordinateBuffer[0];
            previousPixelCoordinate[1] = (int) pixelCoordinateBuffer[1];
            return true;
        }
        return false;
    }

    public int currentSegment(float[] coords) {
        int op = currentSegment(coordinateBuffer);
        coords[0] = (float) coordinateBuffer[0];
        coords[1] = (float) coordinateBuffer[1];
        return op;
    }

    public int currentSegment(double[] coords) {
        previousCoordinate = getCurrentCoordinate();
        coords[0] = previousCoordinate.x;
        coords[1] = previousCoordinate.y;
        if (transform != null && !transform.isIdentity()) {
            transform.transform(coords, 0, coords, 0, 1);
        }
        return determineOperation();
    }

    abstract protected int determineOperation();
}
