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

package org.geolatte.maprenderer.sld;

import net.opengis.se.v_1_1_0.FeatureTypeStyleType;
import org.junit.BeforeClass;

import java.io.InputStream;

public class SLDPainterTest {

    static FeatureTypeStyleType sldRoot;

    FeatureTypeStyle featureTypeStyle;
    FeatureTypeStylePainter painter;



    @BeforeClass
    public static void beforeClass(){
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-sld.xml");
        sldRoot = SLD.instance().unmarshal(in);
    }

    public void before(){
        featureTypeStyle = new FeatureTypeStyle(sldRoot);
        painter = featureTypeStyle.createPainter();
    }


    /**
     * Returns the {@FeatureTypeStylePainter} associated with test-sld.xml
     * @return
     */
    FeatureTypeStylePainter getFeatureTypeStylePainter(){
        return painter;
    }

    /**
     * Returns the FeatureTypeStyle created form test-sld.xml
     */
    public FeatureTypeStyle getFeatureTypeStyle(){
        return featureTypeStyle;        
    }

}
