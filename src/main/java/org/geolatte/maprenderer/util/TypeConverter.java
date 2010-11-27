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
