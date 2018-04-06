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


import org.geolatte.common.Feature;
import org.geolatte.geom.jts.JTS;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;

import java.util.List;

/**
 * A {@link Painter} that paints <code>Feature</code>s according to the
 * instructions in a FeatureTypeStyle.
 *
 * <p>FeatureTypeStyles are specified in the OGC Symbology Encoding Implementation Specification v1.1.0 (05-77r4). </p>
 *
 * <p>In this implementation all rules are applied to each feature in sequence (see 05-77r4, p. 11). This approach is
 * consistent with GeoTools and GeoServer.</p>
 *
 */
public class FeatureTypeStylePainter implements Painter {


    final private List<Rule> rules;
    final private MapGraphics graphics;

    FeatureTypeStylePainter(MapGraphics graphics, List<Rule> rules){
        this.rules = rules;
        this.graphics = graphics;
    }


    //TODO -- this does not take into account the ElseFilters in Rules!!
    @Override
    public void paint(Iterable<Feature> features) {
        //Note: this order (iterate over feature, then iterate over rules is
        // consistent with GeoTools/GeoServer renderers.
        for (Feature feature : features){
            for (Rule rule : rules){
                if (!rule.accepts(feature)) continue;
                rule.symbolize(graphics, feature.getGeometry());
            }
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
