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

import net.opengis.ogc.FilterType;
import net.opengis.se.RuleType;
import net.opengis.se.SymbolizerType;
import org.geolatte.maprenderer.sld.symbolizer.Rule;

import javax.xml.bind.JAXBElement;

public class RuleDirector implements Director<RuleType, Rule> {

    private BuilderFactory builderFactory;
    private RuleBuilder builder;

    public RuleDirector(BuilderFactory builderFactory) {
        setBuilderFactory(builderFactory);
    }

    public void setBuilderFactory(BuilderFactory factory) {
        this.builderFactory = factory;
        this.builder = factory.createRuleBuilder();
    }

    public void construct(RuleType ruleType) {
        FilterType ft = ruleType.getFilter();
        if (ft != null) {
            FilterDirector filterDirector = new FilterDirector(this.builderFactory);
            filterDirector.construct(ft);
            Object filter = filterDirector.getResult();
            builder.setFilter(filter);
        }

        Double maxSD = ruleType.getMaxScaleDenominator();
        if (maxSD != null) {
            builder.setMaxScaleDenominator(maxSD);
        }

        Double minSD = ruleType.getMinScaleDenominator();
        if (minSD != null) {
            builder.setMinScaleDenominator(minSD);
        }

        for (JAXBElement elem : ruleType.getSymbolizer()) {
            Object symbolizer = elem.getValue();
            SymbolizerDirector symDirector = new SymbolizerDirector(this.builderFactory);
            symDirector.construct((SymbolizerType) symbolizer);
            Object constructedSymbolizer = symDirector.getResult();
            this.builder.addSymbolizer(constructedSymbolizer);
        }
    }

    public void setBuilder(RuleBuilder builder) {
        this.builder = builder;
    }


    public Rule getResult() {
        return this.builder.getResult();
    }


}
