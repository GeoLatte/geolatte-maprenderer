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

package org.geolatte.maprenderer.util;

import net.opengis.filter.v_1_1_0.LiteralType;

import javax.xml.bind.JAXBElement;
import java.util.List;

public class JAXBHelper {


    /**
     * Combines all string-elements in a node list.
     * <p/>
     * The node list is assumed to contains either JAXBElements or String elements
     *
     * @param contentList
     * @return
     */
    public static String extractValueToString(List<?> contentList) {
        StringBuilder builder = new StringBuilder();
        extractValueToString(contentList, builder);
        return builder.toString();
    }

    public static void extractValueToString(List<?> contentList, StringBuilder builder) {
        for (Object o : contentList) {
            if (o == null) continue;
            if (o instanceof String) {
                String str = ((String) o).trim();
                builder.append(str);
            } else if (o instanceof JAXBElement) {
                addJAXBElementToValueString((JAXBElement) o, builder);
            }
        }

    }

    private static void addJAXBElementToValueString(JAXBElement element, StringBuilder builder) {
        Object value = element.getValue();
        Class<?> type = element.getDeclaredType();
        if (LiteralType.class.isAssignableFrom(type)) {
            LiteralType literal = LiteralType.class.cast(value);
            extractValueToString(literal.getContent(), builder);
        }
    }
}