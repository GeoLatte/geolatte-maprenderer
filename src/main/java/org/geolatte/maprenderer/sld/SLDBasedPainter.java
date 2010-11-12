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

package org.geolatte.maprenderer.sld;


import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.sld.filter.Filter;
import org.geolatte.maprenderer.sld.symbolizer.Rule;
import org.geolatte.maprenderer.sld.symbolizer.Symbolizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SLDBasedPainter implements Painter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SLDBasedPainter.class);
    private final FeatureTypeStyle style;

    public SLDBasedPainter(FeatureTypeStyle style) {
        this.style = style;
    }

    public void paint(MapGraphics graphics, Iterable<Feature> features) {

        initRules(graphics);

        long cnt = 0;
        long t0 = System.currentTimeMillis();
        for (Feature feature : features) {
            applyRules(style.getRules(), feature);
            cnt++;
        }
        long t1 = System.currentTimeMillis();
        LOGGER.debug(String.format("Painted %d features in %d ms.", cnt, t1 - t0));

    }

    private void initRules(MapGraphics graphics) {
        for (Rule rule : this.style.getRules()) {
            initSymbolizers(graphics, rule);
        }
    }

    private void initSymbolizers(MapGraphics graphics, Rule rule) {
        for (Symbolizer symbolizer : rule.getSymbolizers()) {
            symbolizer.setGraphics(graphics);
        }
    }

    private void applyRules(List<Rule> rules, Feature feature) {
        for (Rule rule : rules) {
            applyRule(rule, feature);
        }
    }

    private void applyRule(Rule rule, Feature feature) {
        Filter filter = rule.getFilter();
        if (filter != null && !filter.evaluate(feature)) {
            return;
        }
        symbolize(rule.getSymbolizers(), feature);
    }

    private void symbolize(List<Symbolizer> symbolizers, Feature feature) {
        for (Symbolizer symbolizer : symbolizers) {
            symbolizer.symbolize(feature);
        }
    }


}
