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

import org.geolatte.core.reflection.Feature;
import org.geolatte.maprenderer.util.DataConvertor;

import java.util.Date;


public class BinaryComparisonOp extends Expr<Boolean, Object> {

    Expr<Object, ?> operand1;
    Expr<Object, ?> operand2;
    Comparison operator;
    boolean matchCase;

    private DataConvertor convertor = new DataConvertor();

    public boolean isMatchCase() {
        return matchCase;
    }

    public void setMatchCase(boolean matchCase) {
        this.matchCase = matchCase;
    }

    public Comparison getOperator() {
        return operator;
    }

    public void setOperator(Comparison operator) {
        this.operator = operator;
    }


    public Boolean evaluate(Feature feature) {
        Object v1 = this.operand1.evaluate(feature);
        Object v2 = this.operand2.evaluate(feature);


        if (v1 instanceof java.util.Date || v2 instanceof java.util.Date) {
            Date d1 = this.convertor.convertToDate(v1);
            Date d2 = this.convertor.convertToDate(v2);
            return eval(d1.compareTo(d2));
        }

        if (v1 instanceof Integer || v1 instanceof Long ||
                v2 instanceof Integer || v2 instanceof Long) {
            Long l1 = this.convertor.convertToLong(v1);
            Long l2 = this.convertor.convertToLong(v2);
            return eval(l1.compareTo(l2));
        }

        if (v1 instanceof Float || v1 instanceof Double ||
                v2 instanceof Float || v2 instanceof Double) {
            Double d1 = this.convertor.convertToDouble(v1);
            Double d2 = this.convertor.convertToDouble(v2);
            return eval(d1.compareTo(d2));
        }

        if (v1 instanceof String || v2 instanceof String) {
            String s1 = this.convertor.convertToString(v1);
            String s2 = this.convertor.convertToString(v2);
            return this.matchCase ? eval(s1.compareTo(s2)) :
                    eval(s1.compareToIgnoreCase(s2));
        }
        throw new RuntimeException("Objects do not match known type.");
    }


    private Boolean eval(int compare) {

        switch (operator) {
            case PropertyIsEqualTo:
                return compare == 0;
            case PropertyIsGreaterThan:
                return compare > 0;
            case PropertyIsGreaterThanOrEqualTo:
                return compare >= 0;
            case PropertyIsLessThan:
                return compare < 0;
            case PropertyIsLessThanOrEqualTo:
                return compare <= 0;
            case PropertyIsNotEqualTo:
                return compare != 0;
            default:
                throw new RuntimeException("Can't interpret operator");
        }
    }

    @Override
    public int getNumArgs() {
        return 2;
    }

    @Override
    public void setArgs(Expr<Object, ?>[] args) {
        this.operand1 = args[0];
        this.operand2 = args[1];
    }

    public String toString() {
        return "(" + this.operand1 + " " + operator + " " + this.operand2 + ")";
    }

}
