package org.geolatte.maprenderer.sld.filter;

import org.geolatte.core.Feature;

/**
 * @author Karel Maesen, Geovise BVBA, 2010
 */
public class AlwaysTrueFilter extends Filter {
    @Override
    public Boolean evaluate(Feature feature) {
        return true;
    }
}
