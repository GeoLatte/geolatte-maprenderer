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
import org.geolatte.maprenderer.sld.filter.AlwaysTrueFilter;
import org.geolatte.maprenderer.sld.filter.ElseFilter;
import org.geolatte.maprenderer.sld.filter.Filter;
import org.geolatte.maprenderer.sld.filter.FilterDecoder;
import org.geolatte.maprenderer.util.JAXBHelper;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FeatureTypeStyle {

    private final List<Rule> rules;
    private final String name;


    FeatureTypeStyle(FeatureTypeStyleType type) {
        this.name = type.getName();
        this.rules = createRules(type);
    }

    public FeatureTypeStylePainter createPainter() {
        return new FeatureTypeStylePainter(rules);
    }

    public String getName() {
        return this.name;
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
        Filter filter = createFilter(ruleType);
        Double minScale = ruleType.getMinScaleDenominator();
        Double maxScale = ruleType.getMaxScaleDenominator();
        return new Rule(ruleType.getName(), filter, minScale, maxScale, symbolizers);
    }

    private Filter createFilter(RuleType ruleType) {
        if (ruleType.getElseFilter() != null) return new ElseFilter();
        if (ruleType.getFilter() == null) return new AlwaysTrueFilter();
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
        }
        symbolizers.add(symbolizer);
    }

    public LineSymbolizer createSymbolizer(LineSymbolizerType type) {
        LineSymbolizer painter = new LineSymbolizer();
        setUOM(type, painter);
        copyGeometryProperty(type, painter);
        copyPerpendicularOffset(type, painter);
        return painter;
    }

    private void copyPerpendicularOffset(LineSymbolizerType type, LineSymbolizer painter) {
        ParameterValueType pv = type.getPerpendicularOffset();
        if (pv == null) return;
        List<Serializable> content = pv.getContent();
        if (content == null || content.isEmpty()) return;
        String valueStr = JAXBHelper.extractValueToString(content);
        Value<Float> value = Value.of(valueStr.toString(), painter.getUOM());
        painter.setPerpendicularOffset(value);
    }

    private void setUOM(SymbolizerType type, AbstractSymbolizer symbolizer) {
        if (type.getUom() != null) {
            UOM uom = UOM.fromURI(type.getUom());
            symbolizer.setUnitsOfMeasure(uom);
        }
    }

    private void copyGeometryProperty(LineSymbolizerType type, LineSymbolizer painter) {
        String geomProp = extractGeometryProperty(type);
        painter.setGeometryProperty(geomProp);
    }

    //XPath expressions or more complex operations are not supported.

    private String extractGeometryProperty(LineSymbolizerType type) {
        if (type.getGeometry() == null) return null;
        if (type.getGeometry().getPropertyName() == null) return null;
        List<Object> list = type.getGeometry().getPropertyName().getContent();
        return JAXBHelper.extractValueToString(list);
    }
}
