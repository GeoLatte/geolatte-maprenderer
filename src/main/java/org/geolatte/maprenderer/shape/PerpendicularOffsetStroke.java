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

import org.geolatte.maprenderer.sld.UOM;
import org.geolatte.maprenderer.sld.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

//TODO -- refactor by splitting perpendicular offset from stroke
//TODO -- this class gives different results from BasicStroke when offset = 0!!

public class PerpendicularOffsetStroke implements Stroke {

    private static Logger LOGGER = LoggerFactory.getLogger(PerpendicularOffsetStroke.class);

    private int join = BasicStroke.JOIN_BEVEL;
    private int cap = BasicStroke.CAP_BUTT;
    private static float FLATNESS = .01f;
    private Value<Float> perpendicularOffset = Value.of(0.f, UOM.PIXEL);
    private float[] dashArray = new float[0];
    private float dashOffset = 0f;

    private float width = 1.0f;

    private double metersPerPixel = 1.0d;


    public PerpendicularOffsetStroke(float width) {
        this.width = width;
    }

    public PerpendicularOffsetStroke(float width, int join, int cap) {
        this(width);
        this.join = join;
        this.cap = cap;
    }

    public PerpendicularOffsetStroke(float width, Value<Float> offset, int join, int cap) {
        this(width, join, cap);
        this.perpendicularOffset = offset;
    }

    public PerpendicularOffsetStroke(float width, Value<Float> offset) {
        this.width = width;
        this.perpendicularOffset = offset;
    }

    public PerpendicularOffsetStroke(float width, Value<Float> offset, int join, int cap, float[] dashArray, float dashOffset) {
        this(width, offset, join, cap);
        this.dashArray = dashArray;
        this.dashOffset = dashOffset;
    }


    public Value<Float> getPerpendicularOffset() {
        return this.perpendicularOffset;
    }

    public float getWidth() {
        return this.width;
    }

    public int getLinejoin() {
        return this.join;
    }


    public int getLinecap() {
        return this.cap;
    }

    public float[] getDashArray() {
        return dashArray;
    }

    public float getDashOffset() {
        return dashOffset;
    }

    /**
     * Creates the stroked shape.
     *
     * <p>If the perpendicalar offset != 0, then the offset linesegments are joined much like the strategy JOIN_MITER.</p>
     * @param shape
     * @return
     */
    public Shape createStrokedShape(Shape shape) {

        BasicStroke stroke = null;
        if (this.dashArray == null || this.dashArray.length == 0) {
            stroke = new BasicStroke((float)(getWidth() * metersPerPixel), this.cap, this.join);
        } else {
            //miter limit 10f is default for BasicStroke.
            float[] scaledDashArray = scaleArray(this.dashArray);
            float scaledDashOffset = (float)(dashOffset * metersPerPixel);
            stroke = new BasicStroke((float)(getWidth() * metersPerPixel), this.cap, this.join, 10.f, scaledDashArray, scaledDashOffset);
        }
        if (this.perpendicularOffset.value() == 0.f) {
            return stroke.createStrokedShape(shape);
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
        double offset = determinePixelOffset();
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

                        LOGGER.debug("offsetAngle = " + 2.0*halfOffsetAngle);
                        LOGGER.debug("halfOffsetAngle = " + halfOffsetAngle);
                        LOGGER.debug("iRadius = " + iRadius);
                        if (offset > 0 && halfOffsetAngle < -Math.PI /4 ||
                                offset < 0 && halfOffsetAngle > Math.PI/4) {
                            //In these cases the offset-lines intersect too far beyond the last point
                            //corect iRadius
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
        return stroke.createStrokedShape(result);
    }

    private float[] scaleArray(float[] dashArray) {
        float[] result = new float[dashArray.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (float)(dashArray[i]*metersPerPixel);
        }
        return result;
    }

    private double determinePixelOffset() {
        float value = this.perpendicularOffset.value();
        if (UOM.PIXEL == this.perpendicularOffset.uom()){
            return value * this.metersPerPixel;
        } else {
            throw new UnsupportedOperationException("Only perpendicular offsets in pixel supported.");
        }
    }

    public void setMetersPerPixel(double metersPerPixel) {
        this.metersPerPixel = metersPerPixel;
    }



}
