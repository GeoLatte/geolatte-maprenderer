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

package org.geolatte.maprenderer.sld.filter;

import net.opengis.filter.v_1_1_0.*;
import org.geolatte.maprenderer.util.JAXBHelper;

import javax.xml.bind.JAXBElement;

public class FilterDecoder {

    private JAXBElement<?> expressionRoot;

    public FilterDecoder(FilterType filterType) {
        if (filterType.getComparisonOps() != null) {
            this.expressionRoot = filterType.getComparisonOps();
            return;
        }
        throw new UnsupportedOperationException();
    }

    public Filter decode() {
        Expression<Boolean, ?> expression = (Expression<Boolean, ?>) parse(expressionRoot);

        Filter filter = new Filter();
        filter.setFilterExpr(expression);
        return filter;
    }

    private Expression<?, ?> parse(JAXBElement<?> element) {

        if (element.getDeclaredType() == BinaryComparisonOpType.class) {
            Comparison comparison = Comparison.valueOf(element.getName().getLocalPart());
            return binaryComparison((BinaryComparisonOpType) element.getValue(), comparison);
        }

        if (element.getDeclaredType() == PropertyNameType.class) {
            return propertyName((PropertyNameType) element.getValue());
        }

        if (element.getDeclaredType() == LiteralType.class) {
            return literal((LiteralType) element.getValue());
        }

        if (element.getDeclaredType() == PropertyIsLikeType.class) {
            return propertyIsLike((PropertyIsLikeType) element.getValue());
        }


        throw new UnsupportedOperationException(element.getDeclaredType().getSimpleName());
    }


    private BinaryComparisonOp binaryComparison(BinaryComparisonOpType operator, Comparison comparison) {
        BinaryComparisonOp op = new BinaryComparisonOp(comparison, operator.isMatchCase());
        int i = 0;
        for (JAXBElement<?> expression : operator.getExpression()) {
            Expression<Object, ?> arg = (Expression<Object, Object>) parse(expression);
            op.setArg(i++, arg);
        }
        return op;
    }

    private LiteralExpression literal(LiteralType literal) {
        LiteralExpression expression = new LiteralExpression();
        expression.SetValue(JAXBHelper.extractValueToString(literal.getContent()));
        return expression;
    }

    private PropertyIsLikeOp propertyIsLike(PropertyIsLikeType element) {
        PropertyIsLikeOp op = new PropertyIsLikeOp(element.getWildCard(), element.getSingleChar(), element.getEscapeChar());
        PropertyExpression property = propertyName(element.getPropertyName());
        LiteralExpression literal = literal(element.getLiteral());
        op.setProperty(property);
        op.setLiteral(literal);
        return op;
    }
//
//    public void addBinaryLogicOp(String operator) {
//        BinaryLogicOp op;
//        if (operator.equalsIgnoreCase("or")) {
//            op = new OrLogicOp();
//        } else {
//            op = new AndLogicOp();
//        }
//        filterTokens.add(op);
//
//    }
//
//    public void addIsBetween() {
//        throw new UnsupportedOperationException("Not implemented");
//
//    }
//
//    public void addIsLikeOperator(String wildCard, String singleChar,
//                                  String escape) {
//        throw new UnsupportedOperationException("Not implemented");
//
//    }
//
//    public void addIsNull() {
//        throw new UnsupportedOperationException("Not implemented");
//    }
//
//    public void addNegation() {
//        NegationExpression op = new NegationExpression();
//        filterTokens.add(op);
//    }


    private PropertyExpression propertyName(PropertyNameType propertyElement) {
        PropertyExpression property = new PropertyExpression();
        String propertyName = JAXBHelper.extractValueToString(propertyElement.getContent());
        property.setPropertyName(propertyName);
        return property;
    }

}
