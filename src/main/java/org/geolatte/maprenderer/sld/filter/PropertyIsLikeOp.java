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
