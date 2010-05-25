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

import org.geolatte.maprenderer.sld.filter.Filter;

/**
 * This interface specifies the contract for builders that cooperate with the
 * FilterDirector class.
 * <p/>
 * The FilterDirector will add the various elements in the order
 * that they appear in the Filter Schema instance.
 *
 * @author maesenka
 */
public interface FilterBuilder extends Builder<Filter> {

    /**
     * Add a comparison operator with two operands.
     *
     * @param operator  The OGC name for the operator.
     * @param matchCase true if String comparisons are case sensitive.
     */
    public void addBinaryComparisonOp(String operator, boolean matchCase);

    /**
     * Add a property expression element
     *
     * @param content the name of the property
     */
    public void addProperty(String content);

    /**
     * Add a literal value
     *
     * @param litVal String representation of the literal
     */
    public void AddLiteral(String litVal);

    /**
     * Add a Like-comparison operator
     *
     * @param wildCard   -the character to be considered as *-wildcard
     * @param singleChar - the chararcter to be considered   ?- wildcard
     * @param escape     - the escape character
     */
    public void addIsLikeOperator(String wildCard, String singleChar, String escape);

    /**
     * Add an isNull test
     */
    public void addIsNull();

    /**
     *
     */
    public void addIsBetween();

    /**
     * Adds a Binary logic operator
     *
     * @param operator the name of the operator (one of "Add" or "Or")
     */
    public void addBinaryLogicOp(String operator);

    /**
     * Adds a negation operator
     */
    public void addNegation();


}
