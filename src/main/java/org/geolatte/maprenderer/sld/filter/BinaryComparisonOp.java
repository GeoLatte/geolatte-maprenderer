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

package org.geolatte.maprenderer.sld.filter;

import org.geolatte.common.Feature;
import org.geolatte.maprenderer.util.TypeConverter;


public class BinaryComparisonOp extends Expression<Boolean, Object> {

    private final Comparison operator;
    private final boolean matchCase;
    Expression<Object, ?> operand1;
    Expression<Object, ?> operand2;

    BinaryComparisonOp(Comparison operator, boolean matchCase) {
        this.operator = operator;
        this.matchCase = matchCase;
    }

    public Boolean evaluate(Feature feature) {
        Object v1 = this.operand1.evaluate(feature);
        Object v2 = this.operand2.evaluate(feature);
        Comparable c1;
        Comparable c2;

        if (v1 instanceof String && !(v2 instanceof String)) {
            c1 = convertToComparable(v1, v2);
            c2 = (Comparable) v2;
        } else if (!(v1 instanceof String) && v2 instanceof String) {
            c2 = convertToComparable(v2, v1);
            c1 = (Comparable) v1;
        } else if (v1 instanceof String && v1 instanceof String) {
            return StringCompare(v1, v2);
        } else {
            c1 = (Comparable) v1;
            c2 = (Comparable) v2;
        }
        return eval(c1.compareTo(c2));
    }

    private boolean StringCompare(Object v1, Object v2) {
        String s1 = (String) v1;
        String s2 = (String) v2;
        return this.matchCase ? eval(s1.compareTo(s1)) : eval(s1.compareToIgnoreCase(s2));
    }

    private Comparable convertToComparable(Object v1, Object v2) {
        return TypeConverter.instance().convert((String) v1, (Class<Comparable>) v2.getClass());
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
    public void setArg(int i, Expression<Object, ?> arg) {
        if (i == 0) this.operand1 = arg;
        if (i == 1) this.operand2 = arg;
    }

    public String toString() {
        return "(" + this.operand1 + " " + operator + " " + this.operand2 + ")";
    }

}
