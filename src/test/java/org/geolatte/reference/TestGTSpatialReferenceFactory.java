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
