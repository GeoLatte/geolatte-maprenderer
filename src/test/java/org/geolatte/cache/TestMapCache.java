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

package org.geolatte.cache;

import org.geolatte.maprenderer.cache.MapCache;
import org.geolatte.maprenderer.cache.MapCacheKey;
import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.geolatte.maprenderer.geotools.GTSpatialReference;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.SpatialReference;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: adnsgis
 * Date: Feb 8, 2010
 * Time: 5:56:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestMapCache {

    private SpatialReference srs;

    @Before
    public void reset() throws SpatialReferenceCreationException {
        MapCache.getInstance().clear();
        srs = new GTSpatialReference("4326", true);
    }

    @Test
    public void test_map_cache_exists() {
        assertNotNull(MapCache.getInstance());
    }

    @Test
    public void test_map_read_write_image() {
        BufferedImage expected = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        MapCacheKey key = new MapCacheKey("aaa", "JPEG", new SpatialExtent(0, 0, 30, 30, srs), expected.getWidth(), expected.getHeight());
        MapCache.getInstance().put(key, expected);
        RenderedImage received = MapCache.getInstance().get(key);
        assertEquals(expected, received);
    }

    @Test
    public void test_clear() {
        BufferedImage expected = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        MapCacheKey key = new MapCacheKey("aaa", "JPEG", new SpatialExtent(0, 0, 256, 256, srs), expected.getWidth(), expected.getHeight());
        MapCache.getInstance().put(key, expected);
        RenderedImage received = MapCache.getInstance().get(key);
        assertEquals(expected, received);
        MapCache.getInstance().clear();
        assertNull(MapCache.getInstance().get(key));
    }

    @Test
    public void test_null_on_cache_miss() {
        MapCacheKey key = new MapCacheKey("aaa", "JPEG", new SpatialExtent(0, 0, 40, 40, srs), 256, 256);
        assertNull(MapCache.getInstance().get(key));
    }


}
