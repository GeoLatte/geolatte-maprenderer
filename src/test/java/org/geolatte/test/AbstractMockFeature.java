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

package org.geolatte.test;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Feature;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Position;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.SingleCoordinateReferenceSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public abstract class AbstractMockFeature implements Feature<C2D, String> {
    protected static AtomicInteger counter = new AtomicInteger(0);

    final private String id;
    final private Map<String, Object> properties;
    final private Geometry<C2D> geom;

    public static final SingleCoordinateReferenceSystem<C2D> CRS = CoordinateReferenceSystems.PROJECTED_2D_METER;


    static private String generateId() {
        int i = counter.incrementAndGet();
        return Integer.toString( i );
    }

    public AbstractMockFeature(String id, Geometry<C2D> geom, Map<String, Object> properties) {
        this.id = id;
        this.properties = properties;
        this.geom = geom;
    }

    public AbstractMockFeature(Geometry<C2D> geom){
        this(generateId(), geom, new HashMap<String, Object>());
    }

    public AbstractMockFeature(Geometry<C2D> geom, Map<String, Object> properties){
        this(generateId(), geom, properties);

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public Geometry<C2D> getGeometry() {
        return geom;
    }
}
