package org.geolatte.maprenderer.sld;

import net.opengis.filter.v_1_1_0.LiteralType;
import net.opengis.se.v_1_1_0.*;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 *
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class FeatureTypeStyle {

    private final FeatureTypeStyleType type;

    FeatureTypeStyle(FeatureTypeStyleType type){
        this.type = type;
    }

    public FeatureTypeStylePainter painter(){
        List<RulePainter> rulePainters = createRulePainters();
        Collections.reverse(rulePainters);
        return new FeatureTypeStylePainter(rulePainters);        
    }

    private List<RulePainter> createRulePainters() {
        List<RulePainter> painters = new ArrayList<RulePainter>();
        List<Object> rulesOrOnlineResources = type.getRuleOrOnlineResource();
        for(Object ruleOrInlineResource : rulesOrOnlineResources){
            if (ruleOrInlineResource instanceof RuleType){
                createAndaddRulePainter((RuleType)ruleOrInlineResource, painters);
            }
        }
        return painters;
    }

    private void createAndaddRulePainter(RuleType ruleType, List<RulePainter> painters) {
        List<SymbolizerPainter> symbolizerPainters = createSymbolizerPainters(ruleType);
        RulePainter painter = new RulePainter(ruleType.getName(), symbolizerPainters);
        painters.add(painter);
    }

    private List<SymbolizerPainter> createSymbolizerPainters(RuleType ruleType) {
        List<SymbolizerPainter> painters = new ArrayList<SymbolizerPainter>();
        for (JAXBElement<? extends SymbolizerType> symbolizerElement : ruleType.getSymbolizer()){
            createAndAddSymbolizerPainter(symbolizerElement.getValue(), symbolizerElement.getDeclaredType(), painters);

        }
        return painters;

    }

    private void createAndAddSymbolizerPainter(SymbolizerType value, Class<? extends SymbolizerType> declaredType, List<SymbolizerPainter> painters) {
        SymbolizerPainter painter = null;
        if (value instanceof LineSymbolizerType){
            painter = createSymbolizerPainter((LineSymbolizerType)value);
        }
        painters.add(painter);        
    }

    public String getName() {
        return type.getName();
    }

    public LineSymbolizerPainter createSymbolizerPainter(LineSymbolizerType type) {
        LineSymbolizerPainter painter = new LineSymbolizerPainter();
        setUOM(type,painter);
        copyGeometryProperty(type, painter);
        copyPerpendicularOffset(type,painter);
        return painter;
    }

    private void copyPerpendicularOffset(LineSymbolizerType type, LineSymbolizerPainter painter) {
        ParameterValueType pv = type.getPerpendicularOffset();
        if (pv == null) return;
        List<Serializable> content = pv.getContent();
        if (content == null || content.isEmpty()) return;
        String valueStr = extractValueToString(content);
        Value<Float> value = Value.of(valueStr.toString(), painter.getUOM());
        painter.setPerpendicularOffset(value);
    }

    private void setUOM(SymbolizerType type, SymbolizerPainter painter) {
        if (type.getUom() != null){
            UOM uom = UOM.fromURI(type.getUom());
            painter.setUnitsOfMeasure(uom);
        }
    }

    private void copyGeometryProperty(LineSymbolizerType type, LineSymbolizerPainter painter) {
        String geomProp = extractGeometryProperty(type);
        painter.setGeometryProperty(geomProp);
    }

    //TODO -- this extraction only works for simple property names.
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
     * @param type
     * @param <T>
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
