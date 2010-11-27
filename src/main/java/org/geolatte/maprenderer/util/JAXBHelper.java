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