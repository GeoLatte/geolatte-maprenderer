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

import net.opengis.se.v_1_1_0.*;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.sld.filter.AlwaysTrueSLDRuleFilter;
import org.geolatte.maprenderer.sld.filter.ElseSLDRuleFilter;
import org.geolatte.maprenderer.sld.filter.FilterDecoder;
import org.geolatte.maprenderer.sld.filter.SLDRuleFilter;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a feature type style, as specified in the Symbology Encoding spec.
 *
 * <p><code>FeatureTypeStyle</code>s are thread-safe.</p>
 */
public class FeatureTypeStyle {

    private final List<Rule> rules;
    private final String name;


    FeatureTypeStyle(FeatureTypeStyleType type) {
        //TODO -- add try/catch here and throw a Checked Exception
        this.name = type.getName();
        this.rules = createRules(type);
    }

    public FeatureTypeStylePainter createPainter(MapGraphics graphics) {
        return new FeatureTypeStylePainter(graphics, getRules());
    }

    public String getName() {
        return this.name;
    }

    public List<Rule> getRules() {
        List<Rule> copy = new ArrayList<Rule>();
        copy.addAll(rules);
        return copy;
    }

    private List<Rule> createRules(FeatureTypeStyleType type) {
        List<Rule> rules = new ArrayList<Rule>();
        List<Object> rulesOrOnlineResources = type.getRuleOrOnlineResource();
        for (Object ruleOrInlineResource : rulesOrOnlineResources) {
            addIfRule(rules, ruleOrInlineResource);
        }
        return rules;
    }

    private void addIfRule(List<Rule> rules, Object ruleOrInlineResource) {
        if (ruleOrInlineResource instanceof RuleType) {
            Rule rule = createRule((RuleType) ruleOrInlineResource, rules);
            rules.add(rule);
        }
    }

    private Rule createRule(RuleType ruleType, List<Rule> rules) {
        List<AbstractSymbolizer> symbolizers = createSymbolizers(ruleType);
        SLDRuleFilter filter = createFilter(ruleType);
        Double minScale = ruleType.getMinScaleDenominator();
        Double maxScale = ruleType.getMaxScaleDenominator();
        return new Rule(ruleType.getName(), filter, minScale, maxScale, symbolizers);
    }

    private SLDRuleFilter createFilter(RuleType ruleType) {
        if (ruleType.getElseFilter() != null) return new ElseSLDRuleFilter();
        if (ruleType.getFilter() == null) return new AlwaysTrueSLDRuleFilter();
        FilterDecoder decoder = new FilterDecoder(ruleType.getFilter());
        return decoder.decode();
    }

    private List<AbstractSymbolizer> createSymbolizers(RuleType ruleType) {
        List<AbstractSymbolizer> symbolizers = new ArrayList<AbstractSymbolizer>();
        for (JAXBElement<? extends SymbolizerType> symbolizerElement : ruleType.getSymbolizer()) {
            createAndAddSymbolizer(symbolizerElement.getValue(), symbolizerElement.getDeclaredType(), symbolizers);

        }
        return symbolizers;
    }

    private void createAndAddSymbolizer(SymbolizerType value, Class<? extends SymbolizerType> declaredType, List<AbstractSymbolizer> symbolizers) {
        AbstractSymbolizer symbolizer = null;
        if (value instanceof LineSymbolizerType) {
            symbolizer = createSymbolizer((LineSymbolizerType) value);
        } else if (value instanceof PolygonSymbolizerType) {
            symbolizer = new PolygonSymbolizer((PolygonSymbolizerType) value);
        } else {
            throw new UnsupportedOperationException("Still to be implemented!");
        }

        symbolizers.add(symbolizer);
    }

    private LineSymbolizer createSymbolizer(LineSymbolizerType type) {
        return new LineSymbolizer(type);
    }

}
