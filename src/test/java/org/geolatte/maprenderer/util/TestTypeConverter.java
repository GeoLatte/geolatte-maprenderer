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
            //OK
        }
    }


}
