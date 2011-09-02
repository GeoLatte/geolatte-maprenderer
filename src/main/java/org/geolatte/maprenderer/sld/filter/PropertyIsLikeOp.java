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

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Nov 27, 2010
 */
public class PropertyIsLikeOp extends Expression<Boolean, String> {

    private PropertyExpression operand1;
    private String regex;
    private final char wildCard;
    private final char singleChar;
    private final char escapeChar;

    PropertyIsLikeOp(String wildCard, String singleChar, String escapeChar) {
        if (wildCard.length() != 1 || singleChar.length() != 1
                || escapeChar.length() != 1)
            throw new IllegalStateException("WildCard, singleChar and escapeChar need to have length 1");
        this.wildCard = wildCard.charAt(0);
        this.singleChar = singleChar.charAt(0);
        this.escapeChar = escapeChar.charAt(0);
    }

    @Override
    public Boolean evaluate(Feature feature) {
        String s1 = (String) operand1.evaluate(feature);
        return s1.matches(regex);
    }

    private String convertToRegex(String inStr) {
        StringBuilder regex = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < inStr.length(); i++) {
            char c = inStr.charAt(i);
            if (c == this.wildCard && !escaped) {
                regex.append(".*");
            } else if (c == this.singleChar && !escaped) {
                regex.append(".");
            } else if (c == this.escapeChar && !escaped) {
                escaped = true;
                continue;
            } else if (!Character.isLetterOrDigit(c)) {
                regex.append("\\").append(c);
            } else {
                regex.append(c);
            }
            escaped = false;
        }
        return regex.toString();
    }

    @Override
    public int getNumArgs() {
        return 2;
    }

    @Override
    public void setArg(int numArg, Expression<String, ?> arg) {
        //not used
    }

    public void setProperty(PropertyExpression property) {
        this.operand1 = property;
    }

    public void setLiteral(LiteralExpression literal) {
        this.regex = convertToRegex(literal.evaluate(null));
    }
}
