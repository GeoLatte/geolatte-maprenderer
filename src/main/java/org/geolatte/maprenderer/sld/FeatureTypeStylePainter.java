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

package org.geolatte.maprenderer.sld;


import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;

import java.util.Collections;
import java.util.List;

public class FeatureTypeStylePainter implements Painter {


    private final List<Rule> rules;

    FeatureTypeStylePainter(List<Rule> rules){
        this.rules = rules;
    }

    @Override
    public void paint(MapGraphics graphics, Iterable<Feature> features) {

    }

    /**
     * Returns the rules in the order defined by the SLD document.
     * @return
     */
    protected List<Rule> getRules() {
        return rules;
    }
}
