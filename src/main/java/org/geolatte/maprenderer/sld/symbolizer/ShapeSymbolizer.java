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

package org.geolatte.maprenderer.sld.symbolizer;

import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.shape.ScalableStroke;
import org.geolatte.maprenderer.shape.ShapeAdapter;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Jan 25, 2010
 * Time: 10:07:46 PM
 * To change this template use File | Settings | File Templates.
 */
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
