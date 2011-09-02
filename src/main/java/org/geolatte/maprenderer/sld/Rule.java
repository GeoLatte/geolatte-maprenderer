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
import org.geolatte.maprenderer.sld.filter.Filter;

import java.util.Collections;
import java.util.List;

public class Rule  {

    private final String name;
    private final List<AbstractSymbolizer> symbolizers;
    private final Filter filter;
    private final Double minScaleDenominator;
    private final Double maxScaleDenominator;

    Rule(String name, Filter filter, Double minScale, Double maxScale, List<AbstractSymbolizer> symbolizers){
        this.name = name;
        this.filter = filter;
        this.symbolizers = symbolizers;
        this.minScaleDenominator = minScale;
        this.maxScaleDenominator = maxScale;
    }

    public String getName() {
        return name;
    }

    protected Filter getFilter() {
        return filter;
    }

    public Double getMinScaleDenominator() {
        return minScaleDenominator;
    }

    public Double getMaxScaleDenominator() {
        return maxScaleDenominator;
    }
}
