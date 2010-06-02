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

import java.awt.*;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class BasicScalableStroke implements ScalableStroke {

    private int join = BasicStroke.JOIN_BEVEL;
    private int cap = BasicStroke.CAP_BUTT;
    private static float FLATNESS = .01f;
    private float perpendicularOffset = 0.f;

    private float width = 1.0f;

    private float scale = 1.0f;


    public BasicScalableStroke(float width, int join, int cap) {
        this.cap = cap;
        this.join = join;
        this.width = width;
    }

    public BasicScalableStroke(float width) {
        this.width = width;
    }


    public float getPerpendicularOffset() {
        return this.perpendicularOffset;
    }

    public float getWidth() {
        return this.width;
    }

    public void setPerpendicularOffset(float pixelDistance) {
        this.perpendicularOffset = pixelDistance;

    }

    public void setWidth(float pixelWidth) {
        this.width = pixelWidth;

    }

    protected float getScale() {
        return this.scale;
    }


    protected int getJoin() {
        return this.join;
    }


    protected int getCap() {
        return this.cap;
    }


    public Shape createStrokedShape(Shape shape) {

        BasicStroke stroke = new BasicStroke(getWidth() / scale, this.cap, this.join);
        if (this.perpendicularOffset == 0.f) {
            return stroke.createStrokedShape(shape);
        }

        GeneralPath result = new GeneralPath();
        PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), FLATNESS);
        float points[] = new float[6];
        float moveX = 0, moveY = 0;
        float lastX = 0, lastY = 0;
        float thisX = 0, thisY = 0;
        float loX = 0, loY = 0;
        double lastOffsetX = 0, lastOffsetY = 0;
        double offsetX= 0, offsetY = 0;
        int type = 0;
        boolean first = true;
        float offset = this.perpendicularOffset / getScale();
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
                    float dx = thisX - lastX;
                    float dy = thisY - lastY;
                    float angle = (float) Math.atan2(dy, dx);
                    offsetX = offset * Math.cos(angle + Math.PI / 2.0);
                    offsetY = offset * Math.sin(angle + Math.PI / 2.0);
                    float nloX = (float) (lastX + offsetX);
                    float nloY = (float) (lastY + offsetY);
                    if (first) {
                        moveX = nloX;
                        moveY = nloY;
                        result.moveTo(moveX, moveY);
                        first = false;
                    } else if (nloX != loX || nloY != loY) {
                        //place intermediate point (iloX,oloY)
                        // halfOffsetAngle is (angle between two consecutive offset vectors)/2
                        //we use the cross product to determine whether the angle is positive (clockwise)
                        // or negative (counter-clockwise)
                        // iRadius is the length of the vector from last point to intermediate point
                        double dotProduct = offsetX * lastOffsetY - offsetY*lastOffsetX;
                        double sign = Math.signum(dotProduct);
                        double halfOffsetAngle = sign*Math.acos((offsetX * lastOffsetX + offsetY * lastOffsetY)/(offset*offset))/2.0;
                        double iRadius =offset * ( Math.cos(halfOffsetAngle) + Math.sin(halfOffsetAngle)*Math.tan(halfOffsetAngle));
                        //TODO -- in very large angles, iRadius can become excessively large. Need to insert two points
                        float iloX = lastX + (float) (iRadius * Math.sin(halfOffsetAngle));
                        float iloY = lastY + (float)(iRadius * Math.cos(halfOffsetAngle));
                        result.lineTo(iloX, iloY);
//                        result.lineTo(nloX, nloY);

                    }

                    loX = nloX + dx;
                    loY = nloY + dy;
//                    result.lineTo(loX, loY);
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

    public void setScale(float scale) {
        this.scale = scale;
    }


}
