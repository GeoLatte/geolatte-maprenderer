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

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Feb 15, 2010
 * Time: 10:46:43 PM
 * To change this template use File | Settings | File Templates.
 */
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
