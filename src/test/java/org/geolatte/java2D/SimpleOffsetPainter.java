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

package org.geolatte.java2D;

import com.vividsolutions.jts.geom.Geometry;
import org.geolatte.core.reflection.Feature;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.shape.BasicScalableStroke;
import org.geolatte.maprenderer.shape.ShapeAdapter;

import java.awt.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class SimpleOffsetPainter implements Painter {


    private final static float WIDTH_IN_PIXELS = 10.0f;
    private final static float OFFSET_IN_PIXELS = 5.0f;


    public void paint(MapGraphics graphics, Iterable<Feature> features) {
        ShapeAdapter shapeAdapter = new ShapeAdapter(graphics.getTransform());

        BasicScalableStroke baseStroke = new BasicScalableStroke(WIDTH_IN_PIXELS);
        baseStroke.setScale(graphics.getScale());

        BasicScalableStroke offsetStroke = new BasicScalableStroke(2.0f);
        offsetStroke.setPerpendicularOffset(OFFSET_IN_PIXELS);
        offsetStroke.setScale(graphics.getScale());

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
