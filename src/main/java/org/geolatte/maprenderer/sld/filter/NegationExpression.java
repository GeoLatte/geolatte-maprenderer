/*
 * Copyright (c) 2011. Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.maprenderer.sld.filter;

import org.geolatte.core.Feature;


public class NegationExpression extends Expression<Boolean, Boolean> {


    private Expression<Boolean, ?> operand = null;


    public void setOperand(Expression<Boolean, ?> expression) {
        this.operand = expression;
    }

    public Boolean evaluate(Feature feature) {
        return !this.operand.evaluate(feature);
    }

    @Override
    public int getNumArgs() {
        return 1;
    }

    @Override
    public void setArg(int i, Expression<Boolean, ?> arg) {
        this.operand = arg;
    }

    public String toString() {
        return "NOT (" + this.operand.toString() + ")";
    }
}
