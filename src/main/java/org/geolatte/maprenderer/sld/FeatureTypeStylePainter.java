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

package org.geolatte.maprenderer.sld;


import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.shape.ShapeAdapter;

import java.awt.*;
import java.util.List;

public class FeatureTypeStylePainter implements Painter {


    final private List<Rule> rules;
    final private MapGraphics graphics;
    final private ShapeAdapter shapeAdapter;


    FeatureTypeStylePainter(MapGraphics graphics, List<Rule> rules){
        this.rules = rules;
        this.graphics = graphics;
        this.shapeAdapter = new ShapeAdapter(graphics.getTransform());
    }

    @Override
    public void paint(Iterable<Feature> features) {
        for (Rule rule : getRules()){
            paint(graphics, features, rule);
        }

    }

    private void paint(MapGraphics graphics, Iterable<Feature> features, Rule rule) {
        if (!rule.withinScaleBounds(graphics)) return;
        for (Feature feature : features) {
            if (!rule.accepts(feature)) continue;
            Shape[] shapes = shapeAdapter.toShape(feature.getGeometry());
            rule.symbolize(graphics,shapes);
        }
    }

    /**
     * Returns the rules in the order defined by the SLD document.
     * @return
     */
    protected List<Rule> getRules() {
        return rules;
    }
}
