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

package org.geolatte.symbology;

import java.awt.*;
import java.io.InputStream;
import java.util.List;

import net.opengis.se.FeatureTypeStyleType;
import org.geolatte.maprenderer.sld.*;
import org.geolatte.maprenderer.sld.FeatureTypeStyle;
import org.geolatte.maprenderer.sld.symbolizer.LineSymbolizer;
import org.geolatte.maprenderer.sld.symbolizer.Rule;
import org.geolatte.maprenderer.sld.symbolizer.Symbolizer;
import org.junit.Test;

 import static org.junit.Assert.*;


public class TestOGCXMLReaderFactory {


    @Test
    public void test_line_symbolizer_from_sld() throws XMLReaderException {

        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test_sld_1.xml");
        XMLReader<FeatureTypeStyleType> reader = OGCXMLReaderFactory.createFeatureTypeStyleReader();
        FeatureTypeStyleType ftst = reader.read(in);
        BuilderFactory factory = new BuilderFactoryImpl();
        FeatureTypeStyleDirector director = new FeatureTypeStyleDirector();
        director.setBuilderFactory(factory);
        director.construct(ftst);
        FeatureTypeStyle style = director.getResult();

        assertEquals("test_lines_style", style.getName());

        List<Rule> rules = style.getRules();
        assertEquals(1, rules.size());
        Rule rule = rules.get(0);
        assertNull(rule.getFilter());        
        Symbolizer symbolizer = rule.getSymbolizers().get(0);
        assertNotNull(symbolizer);
        assertTrue(symbolizer instanceof LineSymbolizer);

        LineSymbolizer lineSymbolizer = (LineSymbolizer)symbolizer;

        assertEquals(Color.decode("#96C3F5"), lineSymbolizer.getStrokeColor());
        assertEquals(1, lineSymbolizer.getStrokeOpacity(), 0.0001);
        assertEquals(3.0, lineSymbolizer.getStroke().getWidth(), 0.0001);
        assertEquals(10.0, lineSymbolizer.getStroke().getPerpendicularOffset(), 0.0001);
        assertEquals("geometry", lineSymbolizer.getGeometryPropertyName());

        

    }

}
