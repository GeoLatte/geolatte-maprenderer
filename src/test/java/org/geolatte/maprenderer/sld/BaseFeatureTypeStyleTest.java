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
import org.geolatte.maprenderer.geotools.GTSpatialReference;
import org.geolatte.maprenderer.java2D.JAIMapGraphics;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.SpatialReference;
import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.junit.BeforeClass;

import java.io.InputStream;

public class BaseFeatureTypeStyleTest {

    static FeatureTypeStyleType sldRoot;
    FeatureTypeStyle featureTypeStyle;

    public MapGraphics createMapGraphics() throws SpatialReferenceCreationException {
        return createMapGraphics(100, 10000);
    }

    public MapGraphics createMapGraphics(int pixelSize, double extentSize) throws SpatialReferenceCreationException {
        SpatialReference spatialReference = new GTSpatialReference("31370", true);
        SpatialExtent extent = new SpatialExtent(0, 0, extentSize, extentSize, spatialReference);
        java.awt.Dimension dim = new java.awt.Dimension(pixelSize, pixelSize);
        MapGraphics mapGraphics =  new JAIMapGraphics(dim, spatialReference, extent);
        return mapGraphics;
    }



    @BeforeClass
    public static void beforeClass() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-sld.xml");
        sldRoot = SLD.instance().unmarshal(in);
    }

    public void before() {
        featureTypeStyle = new FeatureTypeStyle(sldRoot);
    }

    /**
     * Returns the FeatureTypeStyle created form test-sld.xml
     */
    public FeatureTypeStyle getFeatureTypeStyle() {
        return featureTypeStyle;
    }


}