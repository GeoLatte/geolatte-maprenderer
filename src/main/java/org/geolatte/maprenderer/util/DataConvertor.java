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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataConvertor {

    public DataConvertor() {
    }

    public Date convertToDate(Object o) {

        if (o instanceof Date) {
            return (Date) o;
        }
        if (o instanceof Calendar) {
            return ((Calendar) o).getTime();
        }

        String strVal = o.toString();
        try {
            DateFormat df = DateFormat.getInstance();
            return df.parse(strVal);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't parse " + strVal + " to date.", e);
        }
    }


    public Long convertToLong(Object o) {
        if (o instanceof Integer) {
            return new Long((Integer) o);
        }
        if (o instanceof Long) {
            return (Long) o;
        }
        if (o instanceof BigInteger) {
            return ((BigInteger) o).longValue();
        }
        String strVal = o.toString();
        try {
            return Long.parseLong(strVal);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't parse " + strVal + " to Long.", e);
        }
    }

    public Double convertToDouble(Object o) {
        if (o instanceof Float) {
            return new Double((Float) o);
        }
        if (o instanceof Double) {
            return (Double) o;
        }

        if (o instanceof BigDecimal) {
            return ((BigDecimal) o).doubleValue();
        }
        String strVal = o.toString();
        try {
            return Double.parseDouble(strVal);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't parse " + strVal + " to Double.", e);
        }
    }

    public Boolean convertToBoolean(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        String strVal = o.toString();
        try {
            return Boolean.parseBoolean(strVal);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't parse " + strVal + " to Boolean.", e);
        }
    }

    public String convertToString(Object o) {
        if (o instanceof String) {
            return (String) o;
        }
        return o.toString();
    }

}
