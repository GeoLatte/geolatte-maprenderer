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

package org.geolatte.reference;

import org.geolatte.maprenderer.geotools.GTSpatialReferenceFactory;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.Projector;
import org.geolatte.maprenderer.reference.SpatialReference;
import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: adnsgis
 * Date: Feb 1, 2010
 * Time: 3:10:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestGTSpatialReferenceFactory {

    GTSpatialReferenceFactory factory = new GTSpatialReferenceFactory();
    SpatialReference source;
    SpatialReference target;

    @Before
    public void setUp() {
        try {
            source = factory.createSpatialReference("4326", true);
            target = factory.createSpatialReference("3034", true);
        } catch (SpatialReferenceCreationException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void test_transform_extent() {
        Projector projector = factory.createProjector(source, target);
        Projector inverseProjector = factory.createProjector(target, source);
        SpatialExtent mapExtent = new SpatialExtent(-844531.25, 228281.25, 7944531.25, 5501718.75, target);
        SpatialExtent filter = projector.inverseProject(mapExtent);
        assertEquals(source, filter.getSpatialReference());
        SpatialExtent filter2 = inverseProjector.project(mapExtent);
        assertEquals(filter2, filter);
        SpatialExtent back = projector.project(filter);
        assertEquals(target, back.getSpatialReference());
        assertTrue(back.getMinX() <= mapExtent.getMinX());
        assertTrue(back.getMinY() <= mapExtent.getMinY());
        assertTrue(back.getMaxX() >= mapExtent.getMaxX());
        assertTrue(back.getMaxY() >= mapExtent.getMaxY());


    }

}
