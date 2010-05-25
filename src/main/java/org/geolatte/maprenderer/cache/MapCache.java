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

package org.geolatte.maprenderer.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.RenderedImage;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: adnsgis
 * Date: Feb 8, 2010
 * Time: 5:32:45 PM
 * To change this template use File | Settings | File Templates.
 */
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
