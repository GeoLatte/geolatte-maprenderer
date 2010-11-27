package org.geolatte.maprenderer.sld;

import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.sld.filter.Filter;

import java.util.Collections;
import java.util.List;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
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
