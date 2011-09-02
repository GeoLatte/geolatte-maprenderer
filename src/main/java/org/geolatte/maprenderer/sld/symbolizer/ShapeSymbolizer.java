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

package org.geolatte.maprenderer.sld.symbolizer;

import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.shape.ScalableStroke;
import org.geolatte.maprenderer.shape.ShapeAdapter;

import java.awt.*;

public abstract class ShapeSymbolizer extends AbstractSymbolizer {

    private Color strokeColor;
    private float strokeOpacity = 1.0f;
    private ScalableStroke stroke;
    private ThreadLocal<ScalableStroke> threadStroke = new ThreadLocal<ScalableStroke>();
    private Color fillColor;
    private float fillOpacity = 1.0f;
    private ShapeAdapter adapter;
    private ThreadLocal<MapGraphics> graphics = new ThreadLocal<MapGraphics>();
    private double perpendicularOffset;


    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getStrokeOpacity() {
        return strokeOpacity;
    }

    public void setStrokeOpacity(float strokeOpacity) {
        this.strokeOpacity = strokeOpacity;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public float getFillOpacity() {
        return fillOpacity;
    }

    public void setFillOpacity(float fillOpacity) {
        this.fillOpacity = fillOpacity;
    }

    public ScalableStroke getStroke() {
        if (threadStroke.get() == null) {
            threadStroke.set(this.stroke);
        }
        return threadStroke.get();
    }

    public void setStroke(ScalableStroke stroke) {
        this.stroke = stroke;
    }

    public void setPerpendicularOffset(double d) {
        this.perpendicularOffset = d;
    }

    public void setGraphics(MapGraphics graphics) {
        this.graphics.set(graphics);
        setScalableStroke();
        applyOpacity();
        adapter = new ShapeAdapter(graphics.getTransform());
    }

    private void applyOpacity() {
        float fo = getFillOpacity();
        float so = getStrokeOpacity();
        if (fo != 1.0) {
            Color base = this.getFillColor();
            Color newColor = applyColorAlpha(fo, base);
            setFillColor(newColor);
        }
        if (so != 1.0) {
            Color base = this.getStrokeColor();
            Color newColor = applyColorAlpha(so, base);
            setStrokeColor(newColor);
        }
    }

    protected MapGraphics getGraphics() {
        return this.graphics.get();
    }

    protected ShapeAdapter getShapeAdapter() {
        return this.adapter;
    }

    private Color applyColorAlpha(double fo, Color base) {
        float[] components = base.getRGBComponents(null);
        components[components.length - 1] = (float) fo;
        return new Color(components[0], components[1], components[2], components[3]);
    }

    private void setScalableStroke() {
        ScalableStroke stroke = getStroke();
        if (stroke == null) return;
        stroke.setScale(getGraphics().getScale());
        getGraphics().setStroke(stroke);

    }

}
