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

package org.geolatte.cache;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.maprenderer.cache.MapCache;
import org.geolatte.maprenderer.cache.MapCacheKey;
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

    private CrsId crsId;

    @Before
    public void reset() {
        MapCache.getInstance().clear();
        crsId = new CrsId("EPSG",4326);
    }

    @Test
    public void test_map_cache_exists() {
        assertNotNull(MapCache.getInstance());
    }

    @Test
    public void test_map_read_write_image() {
        BufferedImage expected = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        MapCacheKey key = new MapCacheKey("aaa", "JPEG", new Envelope(0, 0, 30, 30, crsId), expected.getWidth(), expected.getHeight());
        MapCache.getInstance().put(key, expected);
        RenderedImage received = MapCache.getInstance().get(key);
        assertEquals(expected, received);
    }

    @Test
    public void test_clear() {
        BufferedImage expected = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        MapCacheKey key = new MapCacheKey("aaa", "JPEG", new Envelope(0, 0, 256, 256, crsId), expected.getWidth(), expected.getHeight());
        MapCache.getInstance().put(key, expected);
        RenderedImage received = MapCache.getInstance().get(key);
        assertEquals(expected, received);
        MapCache.getInstance().clear();
        assertNull(MapCache.getInstance().get(key));
    }

    @Test
    public void test_null_on_cache_miss() {
        MapCacheKey key = new MapCacheKey("aaa", "JPEG", new Envelope(0, 0, 40, 40, crsId), 256, 256);
        assertNull(MapCache.getInstance().get(key));
    }


}
