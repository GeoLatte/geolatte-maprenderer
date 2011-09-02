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

package org.geolatte.maprenderer.sld.filter;

import net.opengis.filter.v_1_1_0.FilterType;
import org.geolatte.core.Feature;
import org.geolatte.maprenderer.sld.SLD;
import org.geolatte.maprenderer.util.ConversionException;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Nov 27, 2010
 */
public class TestFilterDecoder {

    FilterDecoder decoder;

    @Test
    public void test_decoder_property_is_equal() {
        FilterType type = getFragment(TestXML.propertyExpression(Comparison.PropertyIsEqualTo, "100"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(Integer.valueOf(100));
        assertTrue(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn(Integer.valueOf(-100));
        assertFalse(filter.evaluate(mockFeature2));
    }

    @Test
    public void test_decoder_property_is_equal_Boolean() {
        FilterType type = getFragment(TestXML.propertyExpression(Comparison.PropertyIsEqualTo, "true"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(Boolean.TRUE);
        assertTrue(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn(Boolean.FALSE);
        assertFalse(filter.evaluate(mockFeature2));

        Feature mockFeature3 = mock(Feature.class);
        when(mockFeature3.getProperty("size")).thenReturn(Double.valueOf(100d));
        try {
            assertFalse(filter.evaluate(mockFeature3));
            fail();
        } catch (ConversionException e) {
            //OK
        }
    }

    @Test
    public void test_decoder_property_is_equal_Double() {
        FilterType type = getFragment(TestXML.propertyExpression(Comparison.PropertyIsEqualTo, "10.254"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(Double.valueOf(10.254d));
        assertTrue(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn(Double.valueOf(10d));
        assertFalse(filter.evaluate(mockFeature2));
    }

    @Test
    public void test_decoder_property_is_equal_Date() throws ParseException {
        FilterType type = getFragment(TestXML.propertyExpression(Comparison.PropertyIsEqualTo, "2011-10-21"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(df.parse("2011-10-21"));
        assertTrue(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn(df.parse("2011-10-20"));
        assertFalse(filter.evaluate(mockFeature2));
    }


    @Test
    public void test_decoder_property_is_equal_no_matchcase() {
        FilterType type = getFragment(TestXML.PROPERTY_IS_EQUAL_NO_MATCHCASE);
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn("abcdef");
        assertTrue(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn("ABCDEF");
        assertTrue(filter.evaluate(mockFeature2));
    }

    @Test
    public void test_decoder_property_is_not_equal() {
        FilterType type = getFragment(TestXML.propertyExpression(Comparison.PropertyIsNotEqualTo, "100"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(Integer.valueOf(100));
        assertFalse(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn(Integer.valueOf(-100));
        assertTrue(filter.evaluate(mockFeature2));
    }

    @Test
    public void test_decoder_property_is_less_than() {
        FilterType type = getFragment(TestXML.propertyExpression(Comparison.PropertyIsLessThan, "100"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(Integer.valueOf(100));
        assertFalse(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn(Integer.valueOf(99));
        assertTrue(filter.evaluate(mockFeature2));

        Feature mockFeature3 = mock(Feature.class);
        when(mockFeature3.getProperty("size")).thenReturn(Integer.valueOf(101));
        assertFalse(filter.evaluate(mockFeature3));

    }

    @Test
    public void test_decoder_property_is_greater_than() {
        FilterType type = getFragment(TestXML.propertyExpression(Comparison.PropertyIsGreaterThan, "100"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(Integer.valueOf(101));
        assertTrue(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn(Integer.valueOf(90));
        assertFalse(filter.evaluate(mockFeature2));

        Feature mockFeature3 = mock(Feature.class);
        when(mockFeature3.getProperty("size")).thenReturn(Integer.valueOf(100));
        assertFalse(filter.evaluate(mockFeature3));
    }


    @Test
    public void test_decoder_property_is_less_than_or_equal() {
        FilterType type = getFragment(TestXML.propertyExpression(Comparison.PropertyIsLessThanOrEqualTo, "100"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(Integer.valueOf(100));
        assertTrue(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn(Integer.valueOf(99));
        assertTrue(filter.evaluate(mockFeature2));

        Feature mockFeature3 = mock(Feature.class);
        when(mockFeature3.getProperty("size")).thenReturn(Integer.valueOf(101));
        assertFalse(filter.evaluate(mockFeature3));

    }

    @Test
    public void test_decoder_property_is_greater_than_or_equal() {
        FilterType type = getFragment(TestXML.propertyExpression(Comparison.PropertyIsGreaterThanOrEqualTo, "100"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();
        assertNotNull(filter);

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(Integer.valueOf(101));
        assertTrue(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature2.getProperty("size")).thenReturn(Integer.valueOf(90));
        assertFalse(filter.evaluate(mockFeature2));

        Feature mockFeature3 = mock(Feature.class);
        when(mockFeature3.getProperty("size")).thenReturn(Integer.valueOf(100));
        assertTrue(filter.evaluate(mockFeature3));
    }

    @Test
    public void test_property_is_like() {
        FilterType type = getFragment(TestXML.propertyIsLikeExpression("*sub?\\?", "*", "?", "\\"));
        decoder = new FilterDecoder(type);
        Filter filter = decoder.decode();

        Feature mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn("matching suba?");
        assertTrue(filter.evaluate(mockFeature));

        Feature mockFeature2 = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn("nonmatching suba");
        assertFalse(filter.evaluate(mockFeature));

        //confusing wildcards
        type = getFragment(TestXML.propertyIsLikeExpression(".sub*?", ".", "*", "\\"));
        decoder = new FilterDecoder(type);
        filter = decoder.decode();

        mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn("matching suba?");
        assertTrue(filter.evaluate(mockFeature));

        mockFeature2 = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn("nonmatching suba");
        assertFalse(filter.evaluate(mockFeature));

        //tradational wildcards plus escaping
        type = getFragment(TestXML.propertyIsLikeExpression("\\%%sub?\\%", "%", "?", "\\"));
        decoder = new FilterDecoder(type);
        filter = decoder.decode();

        mockFeature = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn("%matching suba%");
        assertTrue(filter.evaluate(mockFeature));

        mockFeature2 = mock(Feature.class);
        when(mockFeature.getProperty("size")).thenReturn(".nonmatching suba%");
        assertFalse(filter.evaluate(mockFeature));
    }


    FilterType getFragment(String fragment) {
        fragment = TestXML.FILTER_ELEMENT_OPEN + fragment + TestXML.FILTER_ELEMENT_CLOSE;
        return SLD.instance().read(fragment, FilterType.class);
    }

    static class TestXML {
        static String FILTER_ELEMENT_OPEN = "<Filter version=\"1.1.0\"" +
                "                  xmlns=\"http://www.opengis.net/ogc\">";
        static String FILTER_ELEMENT_CLOSE = "</Filter>";

        static String PROPERTY_IS_EQUAL_NO_MATCHCASE =
                "<PropertyIsEqualTo matchCase='false'>" +
                        "   <PropertyName>\n" +
                        "       size\n" +
                        "   </PropertyName>" +
                        "   <Literal> abcDEF </Literal>" +
                        "</PropertyIsEqualTo>";

        static String propertyExpression(Comparison comparison, String literal) {
            return "<" + comparison.toString() + ">" +
                    "   <PropertyName>\n" +
                    "       size\n" +
                    "   </PropertyName>" +
                    "   <Literal> " + literal + "</Literal>" +
                    "</" + comparison.toString() + ">";
        }

        static String propertyIsLikeExpression(String literal, String wildCard, String singleChar, String escapeChar) {
            return "<PropertyIsLike wildCard='" + wildCard + "' singleChar='" + singleChar + "' escapeChar='" + escapeChar + "'>" +
                    "   <PropertyName>\n" +
                    "       size\n" +
                    "   </PropertyName>" +
                    "   <Literal> " + literal + "</Literal>" +
                    "</PropertyIsLike>";
        }
    }
}


