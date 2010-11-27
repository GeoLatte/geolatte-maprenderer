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

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Nov 27, 2010
 */
public class TestTypeConverter {

    @Test
    public void test_double_conversion() {
        Double received = TypeConverter.instance().convert("10.33", Double.class);
        assertEquals(Double.valueOf(10.33d), received, Math.ulp(10d));

        try {
            TypeConverter.instance().convert("bla", Double.class);
            fail();
        } catch (ConversionException e) {
            //OK
        }

        received = TypeConverter.instance().convert("1000", Double.class);
        assertEquals(Double.valueOf(1000d), received, Math.ulp(1000d));
    }

    @Test
    public void test_float_conversion() {
        Float received = TypeConverter.instance().convert("10.33", Float.class);
        assertEquals(Float.valueOf(10.33f), received, Math.ulp(10d));
        try {
            TypeConverter.instance().convert("bla", Float.class);
            fail();
        } catch (ConversionException e) {
            //OK
        }
    }

    @Test
    public void test_int_conversion() {
        Integer received = TypeConverter.instance().convert("100", Integer.class);
        assertEquals(Integer.valueOf(100), received);
        try {
            TypeConverter.instance().convert("bla", Integer.class);
            fail();
        } catch (ConversionException e) {
            //OK
        }

        try {
            TypeConverter.instance().convert("1000.12", Integer.class);
            fail();
        } catch (ConversionException e) {
            System.out.println("e = " + e);

        }
    }

    @Test
    public void test_long_conversion() {
        Long received = TypeConverter.instance().convert("100", Long.class);
        assertEquals(Long.valueOf(100), received);
        try {
            TypeConverter.instance().convert("bla", Long.class);
            fail();
        } catch (ConversionException e) {
            //OK
        }

        try {
            TypeConverter.instance().convert("1000.12", Long.class);
            fail();
        } catch (ConversionException e) {
            System.out.println("e = " + e);

        }
    }

    @Test
    public void test_boolean_conversion() {
        Boolean received = TypeConverter.instance().convert("true", Boolean.class);
        assertEquals(Boolean.TRUE, received);
        received = TypeConverter.instance().convert("FALSE", Boolean.class);
        assertEquals(Boolean.FALSE, received);
        //Following the Boolean.parse() behavior, everything that does not parse to True, is returned as False.
        received = TypeConverter.instance().convert("bla", Boolean.class);
        assertEquals(Boolean.FALSE, received);
    }

    @Test
    public void test_date_conversion() throws ParseException {
        Date received = TypeConverter.instance().convert("2010-10-21", Date.class);
        Calendar cal = Calendar.getInstance();
        cal.set(2010, 9, 21, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected = cal.getTime();
        assertEquals(expected, received);

        try {
            TypeConverter.instance().convert("bla", Long.class);
            fail();
        } catch (ConversionException e) {
            //OK
        }

        try {
            TypeConverter.instance().convert("2010-21-10", Long.class);
            fail();
        } catch (ConversionException e) {
            System.out.println("e = " + e);
        }

        try {
            TypeConverter.instance().convert("1000.12", Long.class);
            fail();
        } catch (ConversionException e) {
            System.out.println("e = " + e);

        }
    }


}
