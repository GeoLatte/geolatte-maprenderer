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

import org.geolatte.maprenderer.sld.filter.Filter;
import org.geolatte.maprenderer.sld.symbolizer.AbstractSymbolizer;
import org.geolatte.maprenderer.sld.symbolizer.Rule;

public class RuleBuilderImpl implements RuleBuilder {

    private Rule rule = new Rule();

    public void addSymbolizer(Object object) {
        AbstractSymbolizer symbolizer = (AbstractSymbolizer) object;
        rule.getSymbolizers().add(symbolizer);
    }

    public void setFilter(Object object) {
        Filter filter = (Filter) object;
        rule.setFilter(filter);
    }

    public void setMaxScaleDenominator(Double maxSD) {
        if (maxSD != null)
            rule.setMaxScaleDenominator(maxSD);
    }

    public void setMinScaleDenominator(Double minSD) {
        if (minSD != null)
            rule.setMinScaleDenominator(minSD);

    }

    public Rule getResult() {
        return this.rule;
    }

}
