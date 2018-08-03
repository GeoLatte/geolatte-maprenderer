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

package org.geolatte.maprenderer.cache;

import java.awt.image.RenderedImage;
import java.util.concurrent.atomic.AtomicLong;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MapCache {

	final private static Logger LOGGER = LoggerFactory.getLogger( MapCache.class );

	static {

		//TODO -- move to XML configuration!!
		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache(
						"mapCache",
						CacheConfigurationBuilder.newCacheConfigurationBuilder( MapCacheKey.class, RenderedImage.class,
																				ResourcePoolsBuilder.heap( 100 )
						)
								.build()
				)
				.build( true );

		Cache<MapCacheKey, RenderedImage> ehcache = cacheManager.getCache(
				"mapCache",
				MapCacheKey.class,
				RenderedImage.class
		);
		instance = new MapCache( ehcache );
	}

	final private static MapCache instance;

	final private Cache<MapCacheKey, RenderedImage> ehcache;

	private volatile AtomicLong counter = new AtomicLong();

	private MapCache(Cache<MapCacheKey, RenderedImage> ehcache) {
		this.ehcache = ehcache;
	}

	public static MapCache getInstance() {
		return instance;
	}

	public void put(MapCacheKey key, RenderedImage image) {
		this.ehcache.put( key, image );
	}

	public RenderedImage get(MapCacheKey key) {
		long count = counter.incrementAndGet();
		return this.ehcache.get( key );
	}

	public void clear() {
		this.ehcache.clear();
	}


}
