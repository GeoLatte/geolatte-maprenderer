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

import net.sf.ehcache.CacheManager;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: adnsgis
 * Date: Feb 8, 2010
 * Time: 5:26:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestCacheManager {

    @Test
    public void test_get_cache() {

        CacheManager.create();
        String[] cacheNames = CacheManager.getInstance().getCacheNames();
        for (String s : cacheNames) {
            System.out.println("TestCacheManager.test_get_cache: " + s);
        }

        assertNotNull(CacheManager.getInstance().getCache("mapCache"));
    }
}
