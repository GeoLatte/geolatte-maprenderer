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

package org.geolatte.maprenderer.java2D;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class BasicLabelRect implements LabelRect {

    private final static double SCALE_FACTOR = 0.1;

    private Point2D anchor;

    private TextLayout layout;

    protected BasicLabelRect(String str, Font font, FontRenderContext context) {
        this.layout = new TextLayout(str, font, context);
    }

    public Point2D getAnchor() {
        return this.anchor;
    }

    protected void setAnchor(Point2D anchor) {
        this.anchor = anchor;
        Rectangle2D bnds = this.layout.getBounds();

    }

    public Rectangle2D getBounds() {
        Rectangle2D bounds = layout.getBounds();
        double scl = Math.sqrt(bounds.getHeight() * bounds.getWidth()) * SCALE_FACTOR;
        Point2D pt = getUL();
        bounds.setRect(bounds.getX() + pt.getX() - scl,
                bounds.getY() + pt.getY() - scl,
                bounds.getWidth() + scl * 2.0,
                bounds.getHeight() + scl * 2.0);

        return bounds;
    }

    public TextLayout getTextLayout() {
        return this.layout;
    }

    public void draw(Graphics2D g2) {
        Point2D ul = this.getUL();
        // Point2D ll = this.anchor;
        layout.draw(g2, (float) ul.getX(), (float) ul.getY());
    }

    //get the UL point (position of bounding box and text anchor considering that the anchor is at the center of the layout's bounding box.
    private Point2D getUL() {
        Rectangle2D bnds = this.layout.getBounds();
        return new Point2D.Double(this.anchor.getX() - bnds.getCenterX(), this.anchor.getY() - bnds.getCenterY());
    }

}
