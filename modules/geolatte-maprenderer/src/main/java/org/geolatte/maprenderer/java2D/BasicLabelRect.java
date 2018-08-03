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
