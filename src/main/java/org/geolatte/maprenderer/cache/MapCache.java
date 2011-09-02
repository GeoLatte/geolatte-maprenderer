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

package org.geolatte.maprenderer.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.RenderedImage;
import java.util.concurrent.atomic.AtomicLong;


public class MapCache {

    final private static Logger LOGGER = LoggerFactory.getLogger(MapCache.class);

    private final static int DEBUG_LOG_OUTPUT_EACH = 10;

    static {
        CacheManager.create();
        Cache ehcache = CacheManager.getInstance().getCache("mapCache");
        instance = new MapCache(ehcache);
    }

    final private static MapCache instance;

    final private Cache ehcache;

    private volatile AtomicLong counter = new AtomicLong();

    private MapCache(Cache ehcache) {
        this.ehcache = ehcache;
    }

    public static MapCache getInstance() {
        return instance;
    }

    public void put(MapCacheKey key, RenderedImage image) {
        Element element = new Element(key, image);
        this.ehcache.put(element);
    }

    public RenderedImage get(MapCacheKey key) {
        long count = counter.incrementAndGet();
        Element element = this.ehcache.get(key);
        if (count % DEBUG_LOG_OUTPUT_EACH == 0) LOGGER.debug(getInfoMessage());
        if (element == null) {
            return null;
        }
        return (RenderedImage) element.getObjectValue();
    }

    public void clear() {
        this.ehcache.removeAll();
    }


    public String getInfoMessage() {
        Statistics statistics = this.ehcache.getStatistics();
        long hits = statistics.getCacheHits();
        long misses = statistics.getCacheMisses();
        long size = this.ehcache.getSize();
        long bytes = this.ehcache.getMemoryStoreSize();
        return String.format("hits/misses: %d/%d, items: %d, size (Mbytes): %d", hits, misses, size, bytes / (1024 * 1024));
    }
}
