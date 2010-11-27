package org.geolatte.maprenderer.sld;

import net.opengis.filter.v_1_1_0.LiteralType;
import net.opengis.se.v_1_1_0.*;
import org.geolatte.maprenderer.sld.filter.*;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Style that determines how features are painted.
 *
 *
 *
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class FeatureTypeStyle {

    private final FeatureTypeStyleType type;
    private final List<Rule> rules;

    FeatureTypeStyle(FeatureTypeStyleType type){
        this.type = type;
        this.rules = createRules();
    }

    public FeatureTypeStylePainter createPainter(){
        return new FeatureTypeStylePainter(rules);
    }

    private List<Rule> createRules() {
        List<Rule> rules = new ArrayList<Rule>();
        List<Object> rulesOrOnlineResources = type.getRuleOrOnlineResource();
        for(Object ruleOrInlineResource : rulesOrOnlineResources){
            addIfRule(rules, ruleOrInlineResource);
        }
        return rules;
    }

    private void addIfRule(List<Rule> rules, Object ruleOrInlineResource) {
        if (ruleOrInlineResource instanceof RuleType){
            Rule rule = createRule((RuleType)ruleOrInlineResource, rules);
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
        if (ruleType.getFilter() == null ) return new AlwaysTrueFilter();
        FilterDecoder decoder = new FilterDecoder(ruleType.getFilter());
        return decoder.decode();
    }

    private List<AbstractSymbolizer> createSymbolizers(RuleType ruleType) {
        List<AbstractSymbolizer> symbolizers = new ArrayList<AbstractSymbolizer>();
        for (JAXBElement<? extends SymbolizerType> symbolizerElement : ruleType.getSymbolizer()){
            createAndAddSymbolizer(symbolizerElement.getValue(), symbolizerElement.getDeclaredType(), symbolizers);

        }
        return symbolizers;
    }

    private void createAndAddSymbolizer(SymbolizerType value, Class<? extends SymbolizerType> declaredType, List<AbstractSymbolizer> symbolizers) {
        AbstractSymbolizer symbolizer = null;
        if (value instanceof LineSymbolizerType){
            symbolizer = createSymbolizer((LineSymbolizerType)value);
        }
        symbolizers.add(symbolizer);
    }

    public String getName() {
        return type.getName();
    }

    public LineSymbolizer createSymbolizer(LineSymbolizerType type) {
        LineSymbolizer painter = new LineSymbolizer();
        setUOM(type,painter);
        copyGeometryProperty(type, painter);
        copyPerpendicularOffset(type,painter);
        return painter;
    }

    private void copyPerpendicularOffset(LineSymbolizerType type, LineSymbolizer painter) {
        ParameterValueType pv = type.getPerpendicularOffset();
        if (pv == null) return;
        List<Serializable> content = pv.getContent();
        if (content == null || content.isEmpty()) return;
        String valueStr = extractValueToString(content);
        Value<Float> value = Value.of(valueStr.toString(), painter.getUOM());
        painter.setPerpendicularOffset(value);
    }

    private void setUOM(SymbolizerType type, AbstractSymbolizer symbolizer) {
        if (type.getUom() != null){
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
        return extractValueToString(list);
    }

    /**
     * Combines all string-elements in a node list.
     *
     * The node list is assumed to contains either JAXBElements or String elements
     * @param contentList
     * @return
     */
    protected String extractValueToString(List<?> contentList){
        StringBuilder builder = new StringBuilder();
        extractValueToString(contentList, builder);
        return builder.toString();
    }

    protected void extractValueToString(List<?> contentList, StringBuilder builder){
        for(Object o : contentList){
            if (o == null) continue;
            if (o instanceof String){
                String str = ((String)o).trim();
                builder.append(str);
            } else if (o instanceof JAXBElement){
                addJAXBElementToValueString((JAXBElement)o, builder);
            }
        }

    }

    private void addJAXBElementToValueString(JAXBElement element, StringBuilder builder) {
        Object value = element.getValue();
        Class<?> type = element.getDeclaredType();
        if (LiteralType.class.isAssignableFrom(type)){
            LiteralType literal = LiteralType.class.cast(value);
            extractValueToString(literal.getContent(), builder);
        }
    }
}
