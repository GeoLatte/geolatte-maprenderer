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

import net.opengis.ogc.*;
import org.geolatte.maprenderer.sld.filter.Filter;

import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * The FilterDirector reads a filter and converts it into a new object
 * <p/>
 * <p/>
 * Currently only FilterType schema instances are supported that are
 * limited to: comparison and logic operators, literals and propertynames.
 * <p/>
 * This is class is the director in the builder pattern.
 *
 * @author maesenka
 */
public class FilterDirector implements Director<FilterType, Filter> {

    private FilterBuilder builder = null;

    public FilterDirector() {
    }

    ;

    public FilterDirector(BuilderFactory builderFactory) {
        this.builder = builderFactory.createFilterBuilder();
    }

    public void setBuilderFactory(BuilderFactory builderFactory) {
        this.builder = builderFactory.createFilterBuilder();
    }

    public void construct(FilterType type) {

        JAXBElement<?> ops = type.getComparisonOps();
        if (ops == null) {
            ops = type.getLogicOps();
        }
        if (ops == null) {
            ops = type.getSpatialOps();
        }

        interpret(ops);

//		if (ops != null && ops.size() > 0){
//			JAXBElement<?> elem = ops.get(0);
//			interpret(elem);
//		}
    }

    public Filter getResult() {
        return this.builder.getResult();
    }

    private void interpret(JAXBElement elem) {

        if (elem == null) {
            return;
        }

        Class declaredType = elem.getDeclaredType();

        //Comparison types
        if (declaredType == BinaryComparisonOpType.class) {
            binaryCompOp(elem);
            return;
        }

        if (declaredType == PropertyIsLikeType.class) {
            propertyIsLikeType((PropertyIsLikeType) elem.getValue());
            return;
        }

        if (declaredType == PropertyIsNullType.class) {
            propertyIsNull((PropertyIsNullType) elem.getValue());
            return;
        }

        if (declaredType == PropertyIsBetweenType.class) {
            propertyIsBetween((PropertyIsBetweenType) elem.getValue());
            return;
        }

        //Expression Types
        if (declaredType == PropertyNameType.class) {
            propertyName((PropertyNameType) elem.getValue());
            return;
        }

        if (declaredType == LiteralType.class) {
            literal((LiteralType) elem.getValue());
            return;
        }

        //Logic Operations
        if (declaredType == BinaryLogicOpType.class) {
            binaryLogicOp(elem);
            return;
        }

        if (declaredType == UnaryLogicOpType.class) {
            unaryLogicOp((UnaryLogicOpType) elem.getValue());
            return;
        }

        throw new UnsupportedOperationException("Unsupported element type:" + declaredType);

    }

    private void unaryLogicOp(UnaryLogicOpType negOp) {
        this.builder.addNegation();
        if (negOp.getComparisonOps() != null) {
            interpret(negOp.getComparisonOps());
        } else if (negOp.getLogicOps() != null) {
            interpret(negOp.getLogicOps());
        } else if (negOp.getSpatialOps() != null) {
            interpret(negOp.getSpatialOps());
        }
        return;
    }

    private void binaryLogicOp(JAXBElement elem) {
        String operator = elem.getName().getLocalPart();
        this.builder.addBinaryLogicOp(operator);
        BinaryLogicOpType op = (BinaryLogicOpType) elem.getValue();
        for (JAXBElement operands : op.getOperands()) {
            interpret(operands);
        }
    }

    private void propertyIsBetween(PropertyIsBetweenType between) {
        this.builder.addIsBetween();
        interpret(between.getExpression());
        interpret(between.getLowerBoundary().getExpression());
        interpret(between.getUpperBoundary().getExpression());

    }

    private void propertyIsNull(PropertyIsNullType isNullOp) {
        String propName = isNullOp.getPropertyName().getContent();
        this.builder.addIsNull();
        this.builder.addProperty(propName);
    }

    private void propertyIsLikeType(PropertyIsLikeType isLikeOp) {
        String wildCard = isLikeOp.getWildCard();
        String singleChar = isLikeOp.getSingleChar();
        String escape = isLikeOp.getEscapeChar();
        this.builder.addIsLikeOperator(wildCard, singleChar, escape);
        propertyName(isLikeOp.getPropertyName());
        literal(isLikeOp.getLiteral());
    }

    private void propertyName(PropertyNameType prop) {
        this.builder.addProperty(prop.getContent());
    }


    //TODO == break OGCExpressions out in their own Director - Builder pair.

    private void literal(LiteralType literal) {
        List<Object> contents = literal.getContent();
        String litVal = "";
        if (contents != null && contents.size() > 0) {
            litVal = contents.get(0).toString();
        }
        this.builder.AddLiteral(litVal);
    }


    private void binaryCompOp(JAXBElement elem) {
        BinaryComparisonOpType comp = (BinaryComparisonOpType) elem.getValue();
        String operator = elem.getName().getLocalPart();
        boolean matchCase = comp.isMatchCase();
        this.builder.addBinaryComparisonOp(operator, matchCase);
        if (comp.getExpression().size() == 2) {
            for (JAXBElement expr : comp.getExpression()) {
                interpret(expr);
            }
        }
    }

}
