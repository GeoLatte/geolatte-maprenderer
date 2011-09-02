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
import org.geolatte.maprenderer.sld.filter.SLDRuleFilter;

import java.awt.*;
import java.util.List;

public class Rule  {

    private final String name;
    private final List<AbstractSymbolizer> symbolizers;
    private final SLDRuleFilter filter;
    private final Double minScaleDenominator;
    private final Double maxScaleDenominator;

    Rule(String name, SLDRuleFilter filter, Double minScale, Double maxScale, List<AbstractSymbolizer> symbolizers){
        this.name = name;
        this.filter = filter;
        this.symbolizers = symbolizers;
        this.minScaleDenominator = minScale;
        this.maxScaleDenominator = maxScale;
    }

    public String getName() {
        return name;
    }

    protected SLDRuleFilter getFilter() {
        return filter;
    }

    public Double getMinScaleDenominator() {
        return minScaleDenominator;
    }

    public Double getMaxScaleDenominator() {
        return maxScaleDenominator;
    }

    public boolean accepts(Feature feature) {
        return getFilter().evaluate(feature);
    }

    public boolean withinScaleBounds(MapGraphics graphics) {
        return true;
    }

    public void symbolize(MapGraphics graphics, Shape[] shapes) {
        for (AbstractSymbolizer symbolizer : symbolizers) {
            symbolizer.symbolize(graphics, shapes);
        }
    }

    public List<AbstractSymbolizer> getSymbolizers() {
        return symbolizers;
    }
}
