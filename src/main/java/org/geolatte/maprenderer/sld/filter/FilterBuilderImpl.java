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

import org.geolatte.maprenderer.sld.FilterBuilder;

import java.util.ArrayList;
import java.util.List;

public class FilterBuilderImpl implements FilterBuilder {

    List<Expr<?, ?>> filterTokens = new ArrayList<Expr<?, ?>>();
    int currentExpr = 0;

    public void AddLiteral(String litVal) {
        LiteralExpr expr = new LiteralExpr();
        expr.SetValue(litVal);
        filterTokens.add(expr);
    }

    public void addBinaryComparisonOp(String operator, boolean matchCase) {
        BinaryComparisonOp op = new BinaryComparisonOp();
        op.setMatchCase(matchCase);
        op.setOperator(Comparison.valueOf(operator));
        filterTokens.add(op);
    }

    public void addBinaryLogicOp(String operator) {
        BinaryLogicOp op;
        if (operator.equalsIgnoreCase("or")) {
            op = new OrLogicOp();
        } else {
            op = new AndLogicOp();
        }
        filterTokens.add(op);

    }

    public void addIsBetween() {
        throw new UnsupportedOperationException("Not implemented");

    }

    public void addIsLikeOperator(String wildCard, String singleChar,
                                  String escape) {
        throw new UnsupportedOperationException("Not implemented");

    }

    public void addIsNull() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void addNegation() {
        NegationExpr op = new NegationExpr();
        filterTokens.add(op);
    }

    public void addProperty(String content) {
        PropertyExpr expr = new PropertyExpr();
        expr.setPropertyName(content);
        filterTokens.add(expr);
    }

    @SuppressWarnings("unchecked")
    public Filter getResult() {
        if (filterTokens.isEmpty()) {
            return new Filter();
        }

        Expr<Boolean, ?> root = build();

        Filter filter = new Filter();
        filter.setFilterExpr(root);

        return filter;
    }

    @SuppressWarnings("unchecked")
    private Expr build() {
        Expr expression = filterTokens.get(this.currentExpr);
        int numArgs = expression.getNumArgs();
        if (numArgs == 0) {
            return expression;
        }
        Expr[] operands = new Expr[numArgs];
        for (int i = 0; i < numArgs; i++) {
            currentExpr++;
            operands[i] = build();
        }
        expression.setArgs(operands);
        return expression;
    }

}
