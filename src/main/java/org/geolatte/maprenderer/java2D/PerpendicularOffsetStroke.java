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

package org.geolatte.maprenderer.java2D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

//Since the DuctusShapeRenderer in Oracle's Java2D implementation handles BasicStroke and any other strokes differently,
// the approach here is not ideal. It produces different rendering results from BasicStroke when pOffset == 0.
//therefore:
//TODO -- move the calculation of the offset to a the GeometryPathIterator.

public class PerpendicularOffsetStroke implements Stroke {

    private static Logger LOGGER = LoggerFactory.getLogger(PerpendicularOffsetStroke.class);

    public static double EPSILON = 0.025d;

    private static float FLATNESS = .01f;
    private float perpendicularOffset = 0f;
    private BasicStroke delegate;

    public PerpendicularOffsetStroke(float width) {
        delegate = new BasicStroke(width);
    }

    public PerpendicularOffsetStroke(float width, int join, int cap) {
        delegate = new BasicStroke(width, cap, join);
    }

    public PerpendicularOffsetStroke(float width, float offset, int join, int cap) {
        delegate = new BasicStroke(width, cap, join);
        this.perpendicularOffset = offset;
    }

    public PerpendicularOffsetStroke(float width, float offset) {
        delegate = new BasicStroke(width);
        this.perpendicularOffset = offset;
    }

    public PerpendicularOffsetStroke(float width, float offset, int join, int cap, float[] dashArray, float dashOffset) {
        delegate = new BasicStroke(width, cap, join, 10f, dashArray, dashOffset);
        this.perpendicularOffset = offset;
    }


    public float getPerpendicularOffset() {
        return this.perpendicularOffset;
    }

    /**
     * Creates the stroked shape.
     *
     * <p>If the perpendicalar offset != 0, then the offset linesegments are joined much like the strategy JOIN_MITER.</p>
     *
     *
     * @param shape
     * @return
     */
    public Shape createStrokedShape(Shape shape) {

        if (Math.abs(this.perpendicularOffset) < Math.ulp(1.f)) {
            return delegate.createStrokedShape(shape);
        }

        GeneralPath result = new GeneralPath();
        PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), FLATNESS);
        float points[] = new float[6];

        float moveX = 0, moveY = 0;
        // point (lasX, lasty) is the point that the PathIterator pointed to in the previous iteration
        float lastX = 0, lastY = 0;
        // point (thisX, thisY) is the point that the pathIterator currently points to
        float thisX = 0, thisY = 0;
        // vector (lastOffsetX, lastOffsetY) is the previous offset vector (orthogonal to the vector
        // determined by last and before last point)
        double lastOffsetX = 0, lastOffsetY = 0;
        // vector (offsetX, offsetY) is the current offset vector (orthogonal to the vector
        // determined by last and current point)
        double offsetX= 0, offsetY = 0;
        // the point (loX, loY) is the current point + the previous offset-vector
        float loX = 0, loY = 0;
        int type = 0;
        boolean first = true;
        double offset = perpendicularOffset;
        while (!it.isDone()) {
            type = it.currentSegment(points);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    moveX = lastX = points[0];
                    moveY = lastY = points[1];
                    first = true;
                    break;

                case PathIterator.SEG_CLOSE:
                    points[0] = moveX;
                    points[1] = moveY;
                    // Fall into....

                case PathIterator.SEG_LINETO:

                    thisX = points[0];
                    thisY = points[1];
                    //(dx,dy) is the vector from last point to the current point
                    float dx = thisX - lastX;
                    float dy = thisY - lastY;
                    // segmentAngle is the angle of the linesegment between last and current points
                    float segmentAngle = (float) Math.atan2(dy, dx);
                    LOGGER.debug("segment-angle = " + segmentAngle);
                    offsetX = offset * Math.cos(segmentAngle + Math.PI / 2.0);
                    offsetY = offset * Math.sin(segmentAngle + Math.PI / 2.0);
                    // point (nloX, nloY) is last point + current offset vector
                    float nloX = (float) (lastX + offsetX);
                    float nloY = (float) (lastY + offsetY);
                    if (first) {
                        moveX = nloX;
                        moveY = nloY;
                        result.moveTo(moveX, moveY);
                        first = false;
                    } else if (nloX != loX || nloY != loY) {
                        // the formula for the signed angle between two vectors: ang = atan2(x1*y2-y1*x2,x1*x2+y1*y2
                        double angleBetweenOffsetVectors = Math.atan2( lastOffsetX*offsetY - lastOffsetY*offsetX, lastOffsetX*offsetX + lastOffsetY*offsetY);
                        double halfOffsetAngle = angleBetweenOffsetVectors / 2;
                        //iRadius is the length of the vector along the bisector of the two consecutive offset vectors that starts
                        // at the last point, and ends in the intersection of the two offset lines.
                        double iRadius = offset / Math.cos(halfOffsetAngle);

                        LOGGER.debug("offset = " + offset);
                        LOGGER.debug("offsetAngle = " + 2.0*halfOffsetAngle);
                        LOGGER.debug("halfOffsetAngle = " + halfOffsetAngle);
                        LOGGER.debug("iRadius = " + iRadius);
                        if (offset > 0 && halfOffsetAngle < (Math.PI/2 + EPSILON) && halfOffsetAngle > (Math.PI/2 - EPSILON) ||
                                offset < 0 && halfOffsetAngle > (  - Math.PI/2 - EPSILON) && halfOffsetAngle < (- Math.PI/2 + EPSILON)) {
                            LOGGER.debug("CORNER CASE");
                            // do nothing
                        } else if (offset > 0 && halfOffsetAngle < -Math.PI /4 ||
                                offset < 0 && halfOffsetAngle > Math.PI/4) {
                            //In these cases the offset-lines intersect too far beyond the last point
                            //correct iRadius
                            iRadius = offset/ Math.cos(Math.PI/4);
                            float iloX = lastX + (float)(iRadius * Math.cos(segmentAngle + Math.PI/2 - 2*halfOffsetAngle - Math.signum(offset) * Math.PI/4));
                            float iloY = lastY + (float)(iRadius * Math.sin(segmentAngle + Math.PI/2 - 2*halfOffsetAngle - Math.signum(offset) * Math.PI/4));
                            result.lineTo(iloX, iloY);
                            iloX = lastX + (float)(iRadius * Math.cos(segmentAngle + Math.PI/2 + Math.signum(offset) *Math.PI/4));
                            iloY = lastY + (float)(iRadius * Math.sin(segmentAngle + Math.PI/2 + Math.signum(offset) *Math.PI/4));
                            result.lineTo(iloX, iloY);
                        } else {
                            float iloX = lastX + (float) (iRadius * Math.cos(segmentAngle + Math.PI/2 - halfOffsetAngle));
                            float iloY = lastY + (float)(iRadius * Math.sin(segmentAngle + Math.PI/2 - halfOffsetAngle));
                            result.lineTo(iloX, iloY);
                        }

                    }

                    loX = nloX + dx;
                    loY = nloY + dy;
                    lastX = thisX;
                    lastY = thisY;
                    lastOffsetX = offsetX;
                    lastOffsetY = offsetY;
                    break;
            }
            it.next();
        }
        result.lineTo(loX, loY);        
        return delegate.createStrokedShape(result);
    }

    //Delegate methods


    public float getLineWidth() {
        return delegate.getLineWidth();
    }

    public int getEndCap() {
        return delegate.getEndCap();
    }

    public int getLineJoin() {
        return delegate.getLineJoin();
    }

    public float getMiterLimit() {
        return delegate.getMiterLimit();
    }

    public float[] getDashArray() {
        return delegate.getDashArray();
    }

    public float getDashPhase() {
        return delegate.getDashPhase();
    }
}
