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

package org.geolatte.maprenderer.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Nov 27, 2010
 */
public class TypeConverter {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private static TypeConverter instance = new TypeConverter();
    Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    public static TypeConverter instance() {
        return instance;
    }

    public <T extends Comparable> T convert(String str, Class<T> targetClass) {
        Converter converter = getConverter(targetClass);
        if (converter == null)
            throw new UnsupportedOperationException("Can't convert between string and " + targetClass.getCanonicalName());
        return (T) converter.convert(str);
    }

    private Converter getConverter(Class<?> targetClass) {
        return this.converters.get(targetClass);
    }

    private TypeConverter() {
        converters.put(Integer.class, new Converter<Integer>() {
            @Override
            Integer convert(String str) {
                try {
                    return Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    throw new ConversionException(e);
                }
            }
        });

        converters.put(Float.class, new Converter<Float>() {
            @Override
            Float convert(String str) {
                try {
                    return Float.parseFloat(str);
                } catch (NumberFormatException e) {
                    throw new ConversionException(e);
                }
            }
        });

        converters.put(Double.class, new Converter<Double>() {
            @Override
            Double convert(String str) {
                try {
                    return Double.parseDouble(str);
                } catch (NumberFormatException e) {
                    throw new ConversionException(e);
                }
            }
        });

        converters.put(Long.class, new Converter<Long>() {
            @Override
            Long convert(String str) {
                try {
                    return Long.parseLong(str);
                } catch (NumberFormatException e) {
                    throw new ConversionException(e);
                }
            }
        });

        converters.put(Boolean.class, new Converter<Boolean>() {
            @Override
            Boolean convert(String str) {
                return Boolean.parseBoolean(str);
            }
        });

        converters.put(Date.class, new Converter<Date>() {
            @Override
            Date convert(String str) {
                DateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                try {
                    return df.parse(str);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }


    abstract static class Converter<T> {
        abstract T convert(String str);
    }
}
