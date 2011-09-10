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

package org.geolatte.java2D;

import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.shape.BasicScalableStroke;
import org.geolatte.maprenderer.shape.ShapeAdapter;
import org.geolatte.maprenderer.sld.UOM;
import org.geolatte.maprenderer.sld.Value;

import java.awt.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class SimpleOffsetPainter implements Painter {


    private final static float WIDTH_IN_PIXELS = 10.0f;
    private final static Value<Float> OFFSET_IN_PIXELS = Value.of(5.0f, UOM.PIXEL);

    private final MapGraphics graphics;

    public SimpleOffsetPainter(MapGraphics graphics){
        this.graphics = graphics;
    }

    public void paint(Iterable<Feature> features) {
        ShapeAdapter shapeAdapter = new ShapeAdapter(graphics.getTransform());

        BasicScalableStroke baseStroke = new BasicScalableStroke(WIDTH_IN_PIXELS);

        BasicScalableStroke offsetStroke = new BasicScalableStroke(2.0f, OFFSET_IN_PIXELS);

        for(Feature feature : features){
            Shape[] shapes = shapeAdapter.toShape(feature.getGeometry());
            for (Shape shape : shapes){
                graphics.setColor(Color.RED);
                graphics.setStroke(baseStroke);
                graphics.draw(shape);
                graphics.setColor(Color.BLACK);
                graphics.setStroke(offsetStroke);
                graphics.draw(shape);
            }
        }

    }
}
