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
public class RulePainter implements Painter {

    private final String name;
    private final List<SymbolizerPainter> symbolizerPainters;
    private Filter filter;

    RulePainter(String name, List<SymbolizerPainter> symbolizers){
        this.name = name;
        this.symbolizerPainters = symbolizers;
    }

    @Override
    public void paint(MapGraphics graphics, Iterable<Feature> features) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getName() {
        return name;
    }

    public List<SymbolizerPainter> getSymbolizerPainters() {
        return Collections.unmodifiableList(symbolizerPainters);
    }

    public Filter getFilter() {
        return filter;
    }
}
